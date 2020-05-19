### 线程池
线程池使用的生成者-消费者模式，线程池的使用方时生产者，而线程池本身是消费者

### ThreadPoolExecutor 原理
研究原理之前，首先熟悉下ThreadPoolExecutor基本应用

* 构造方法
    
        ThreadPoolExecutor(int corePoolSize,  // 核心线程数，空闲下来这些线程也不会被回收，当然可以配置allowCoreThreadTimeOut(boolean) 设置是否对核心线程有效
                  int maximumPoolSize, // 最大线程数，当核心线程数满了，工作队列满了，就会创建新的线程，直到达到最大线程数，如果达到最大线程数还有任务进来，就会使用拒绝策略
                  long keepAliveTime,  // 线程空闲时存活时间（非核心线程）
                  TimeUnit unit, // 存活时间单位
                  BlockingQueue<Runnable> workQueue, // 任务队列
                  ThreadFactory threadFactory,  // 线程工厂
                  RejectedExecutionHandler handler) // 拒绝策略
     
      举个显示中的例子，比如工厂有10个员工做业务，排队的业务较多，当多于10个待业务时，其它在来的任务就要排队了，当排队的任务较多，并且排队速度远大于处理速度，比如超过 10 个，
      就要在招员工了，招了 5 个临时员工，再来任务就要交给这 5 个员工处理了，当这 15 个人还处理不及时，就要选择抛弃一些任务了。当这个 15 员工空闲时，而待处理的任务又非常少时
      就要选择辞掉 这 5 个临时工了
      以上 10 个核心员就表示 "核心线程"了，15 个员工就表示最大线程数了，排队就是任务队列，抛弃就是对应着拒绝策略了
      以上7个形参，任务队列以及线程工厂和拒绝策略都是建议自己实现接口，定制自己所需的逻辑。有一点需要说明，刚开始建好的线程池是没有活动线程的（可以调用prestartAllCoreThreads()
      或者prestartCoreThread() 来提前初始化线程），有任务进来再创建，当创建的线程数达到核心线程数【corePoolSize】时，就会将任务放到【阻塞队列】，队列满了，并且线程数量小于
      最大线程，就会在创建线程，直到达到最大线程数量，如果达到最大线程数量还是继续有任务，就会执行【拒绝策略】了
 

* 任务队列
 
        常用的BlockingQueue 有：
        ArrayBlockingQueue 基于数组的FIFO队列，构造时必须显示传入长度
        LinkedBlockingQueue 基于链表的FIFO队列，不指定长度默认Integer.MAX_VALUE
        SynchronousQueue 这个队列不保存提交的任务，而是直接新建一个线程执行任务
 
* 拒绝策略
    
      默认拒绝策略有：
      ThreadPoolExecutor.AbortPolicy  丢弃任务并抛出：RejectedExecutionException
      ThreadPoolExecutor.DiscardPolicy 丢弃任务并不会抛出异常
      ThreadPoolExecutor.DiscardOldestPolicy 丢弃队列最前的任务，然后重新尝试执行任务
      ThreadPoolExecutor.CallerRunsPolicy 由调用线程处理该任务
      还可以实现接口，自定义拒绝策略，比如异步写到MQ里，异步消费处理
 
* 线程池启动&终止
  
      线程池执行任务/启动 有两个方法 execute() 和 submit() 前者需要需要实力类实现Runnable接口，后者支持实现了Callable的实例类，这个接口用途和Runnable一样，
      只是多了返回值和异常处理。前者没有返回值，后者用Future 作为返回，可以接收线程池执行任务的返回信息，底层也是使用的execute()
      线程池终止有两个方法 shutdown() shutdownNow() 前者不会立即终止线程，只是不再接口新任务，还会等待队列里或者正在执行的任务执行完毕，而后者是直接中断线程
      可能会存在任务丢失等异常情况，不要使用后者
      
### 问题

* 1.线程池是如何管理线程的
    维护的线程队列
* 2.拒绝策略是怎么触发的
    execute执行时，来任务首先创建核心线程，达到核心线程任务入队，队满创建最大线程，达到最大线程就执行拒绝策略
* 3.空闲的非核心线程（核心线程-设置了需要回收核心线程）是如何回收的
* 4.任务队列排队策略
    先进先出
* 5.队列里的任务是如何被执行的
    由运行的线程执行完任务只会自旋从任务队列里获取任务执行，避免了单独创建线程来分配任务

### 源码解析

线程池的5种状态：

    private static final int RUNNING    = -1 << COUNT_BITS;  // 运行
    private static final int SHUTDOWN   =  0 << COUNT_BITS;  // 调用了shutdown()
    private static final int STOP       =  1 << COUNT_BITS;  // 停止
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;  // 正常运行结束

线程池主要的方法还是提交任务，execute() submit() sumbit() 底层调用的execute()，带着以上问题以及用法来阅读源码

    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        // 工作的线程数量
        int c = ctl.get();
        // 一.数量小于核心线程，直接创建线程来执行任务
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true)) {
                return;
            }
            c = ctl.get();
        }
        // 二.工作线程超过核心线程，任务直接入队列
        if (isRunning(c) && workQueue.offer(command)) {
            // 重新获取线程池状态，因为是支持多线程的，可能线程池状态已经发生了变化，重新获取
            int recheck = ctl.get();
            // 线程状态不是RUNNING，说明执行过shutdown() 将新加入的任务reject
            if (! isRunning(recheck) && remove(command)) {
                reject(command);
            }
            // 这里是因为线程池核心线程数量允许为0，所以加判断
            else if (workerCountOf(recheck) == 0) {
                addWorker(null, false);
            }
        }
        // 如果线程不是运行状态，且入队失败，就要重新创建线程执行任务
        // 注意：
        // 1.线程池不是运行状态是，addWorker 内部会判断
        // 2.addWorker 第二个参数表示是否创建核心线程
        // 三.3.创建线程失败（addWorker 返回false），执行拒绝策略
        else if (!addWorker(command, false)) {
            reject(command);
        }
    }

创建Worker（线程）执行任务

    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        // 自旋
        for (;;) {
            // 获取工作线程数
            int c = ctl.get();
            // 获取线程池状态
            int rs = runStateOf(c);

            // 线程池状态大于等于shutdown，返回false；如果处于shutdown状态，且传入任务为空返回false；处于shutdown，并且队列空，返回false
            if (rs >= SHUTDOWN &&
                ! (rs == SHUTDOWN &&
                   firstTask == null &&
                   ! workQueue.isEmpty()))
                return false;

            // 自旋通过cas增加运行线程数量
            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                    wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        // 创建线程执行任务
        // 是否成功启动Worker线程
        boolean workerStarted = false;
        // 是否成功创建Worker线程
        boolean workerAdded = false;
        Worker w = null;
        try {
            // 创建Worker，内部成员变量Thread，构造方法通过ThreadFactory创建线程
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                // 串行创建Worker
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // 再次判断线程池状态
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        // 幂等操作，已经创建过，不在创建
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            // 记录最大线程数量
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                // 线程启动，执行任务
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            // 启动失败，说明线程池状态变化（执行了shutdown）
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }

Worker 单元

    private final class Worker extends AbstractQueuedSynchronizer implements Runnable
    {
        private static final long serialVersionUID = 6138294804551838833L;

        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        // 创建Worker，调用ThreadFactory 的方法创建线程
        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        public void run() {
            runWorker(this);
        }
        // 省略代码
    }

runworker() 线程空闲时从队列里取任务（每个线程都会去取）

    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        // 可以让外部中断（可以响应中断）
        w.unlock();
        // 用来判断是否进入过自旋
        boolean completedAbruptly = true;
        try {
            // 当前任务是否为null，不为null取队列，如果队列空，则阻塞
            while (task != null || (task = getTask()) != null) {
                // 加锁，保证Worker执行是串行的，锁细粒度化，提升性能
                w.lock();
                // 如果线程池正在停止，当前线程中断操作
                if ((runStateAtLeast(ctl.get(), STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted()) {
                    wt.interrupt();
                }
                try {
                    // 扩展设计，执行任务前
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        // 执行任务
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        // 扩展设计，执行任务后
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    // 执行成功任务次数+1
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            // 自旋退出，说明线程池正在停止，回收非核心线程，如果设置了allowCoreThreadTimeOut = true，那么将回收核心线程
            processWorkerExit(w, completedAbruptly);
        }
    }
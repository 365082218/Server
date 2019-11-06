package idevgame.meteor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OrderedThreadPoolExecutor閺勵垰寮懓鍍磂tty3閻ㄥ嚠rderedMemoryAwareThreadPoolExecutor鐎圭偟骞囬惃鍕箒鎼村繒鍤庣粙瀣潨閿涳拷<b>濞屸剝婀丮emoryAware閻ㄥ嫬鐤勯悳锟�</b>
 * <p/>
 * <ul>
 * <li>缁捐法鈻煎Ч鐘辩窗绾喕绻氶惄绋挎倱key閻ㄥ嚧unable閹稿鍙巈xecute閻ㄥ嫰銆庢惔蹇斿⒔鐞涳拷</li>
 * <li>缁捐法鈻煎Ч鐘辩瑝娴兼矮绱扮涵顔荤箽閻╃鎮撻惃鍒眅y閻ㄥ嚧unable閹稿鍙庢慨瀣矒閸︺劌鎮撴稉锟芥稉顏嗗殠缁嬪鍞撮幍褑顢�</li>
 * <li>缁捐法鈻煎Ч鐘辩瑝娴兼矮绱扮涵顔荤箽娑撳秴鎮撻惃鍒眅y閻ㄥ嚧unable閹稿鍙巈xecute閻ㄥ嫰銆庢惔蹇斿⒔鐞涳拷</li>
 * <li>婵″倹鐏夐弻鎰嚋閻╃鎮搆ey閻ㄥ嚧unable瀵板牆顦块敍灞肩窗鐎佃壈鍤х痪璺ㄢ柤濮圭姴鍞撮幍褑顢戠拠顧眅y閻ㄥ嫮鍤庣粙瀣毐閺冨爼妫跨悮顐㈠窗閻拷</li>
 * <li>閸氬本妞傞梻纾嬬箖婢舵氨娈憈ask閸欘垵鍏樼�佃壈鍤ч崘鍛摠濞夊嫰婀堕敍渚婄磼閿涗焦顒濇径鍕讲閼虫垝绱扮悮顐㈠焺閻€劌浠沨ash閺�璇插毊閿涗緤绱掗敍锟�</li>
 * </ul>
 *
 * @author moon
 * @see net.moon.frame.utils.OrderedThreadPoolExecutor.OrderedRunable
 */
public final class OrderedThreadPoolExecutor extends ForkJoinPool {

	private static Logger logger = LoggerFactory.getLogger(OrderedThreadPoolExecutor.class);

	private final static int DEFAULT_NUM_EXECUTOR = 1024;
	private final static int DEFAULT_BATCH_LIMIT = 5;

	/**
	 * executors
	 */
	private final ChildExecutor[] childExecutors;

	/**
	 * the limit of batch process tasks
	 */
	private final int batchLimit;

	/**
	 * 缁鎶�娴滐拷 Executors.newFiexedThreadPool() 濮樻瓕绻欐穱婵囧瘮娑擄拷鐎规氨娈戠痪璺ㄢ柤濮圭姴銇囩亸锟�
	 *
	 * @param corePoolSize 缁捐法鈻煎Ч鐘层亣鐏忥拷
	 * @return OrderedThreadPoolExecutor
	 */
	public static OrderedThreadPoolExecutor newFixesOrderedThreadPool(int corePoolSize) {
		return newFixesOrderedThreadPool(corePoolSize, DEFAULT_NUM_EXECUTOR, DEFAULT_BATCH_LIMIT);
	}

	/**
	 * 缁鎶�娴滐拷 Executors.newFiexedThreadPool() 濮樻瓕绻欐穱婵囧瘮娑擄拷鐎规氨娈戠痪璺ㄢ柤濮圭姴銇囩亸锟�
	 *
	 * @param corePoolSize  缁捐法鈻煎Ч鐘层亣鐏忥拷
	 * @param numOfExecutor executor閻ㄥ嫭鏆熼柌锟�
	 * @param batchLimit    閹靛綊鍣洪幍褑顢戞禒璇插閿涘奔绻氱拠涔獂ecutor閻ㄥ嫬鍙曢獮铏拷锟�
	 * @return OrderedThreadPoolExecutor
	 */
	public static OrderedThreadPoolExecutor newFixesOrderedThreadPool(int corePoolSize, int numOfExecutor, int batchLimit) {
		logger.info("!!! init " + corePoolSize + " core OrderedThreadPoolExecutor");
		return new OrderedThreadPoolExecutor(
				corePoolSize, numOfExecutor, batchLimit
		);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param numOfExecutor the number of executor
	 * @param batchLimit    the limit of batch process tasks
	 */
	private OrderedThreadPoolExecutor(int corePoolSize, int numOfExecutor, int batchLimit) {

		super(corePoolSize, ForkJoinPool.defaultForkJoinWorkerThreadFactory, new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error(e.getMessage(), e);
			}
		}, true);

		this.childExecutors = new ChildExecutor[numOfExecutor];
		for (int i = 0; i < this.childExecutors.length; ++i) {
			this.childExecutors[i] = new ChildExecutor(i, "corePoolSize_"+corePoolSize);
		}

		this.batchLimit = batchLimit;
	}

	@Override
	public void execute(Runnable task) {
		if (task instanceof OrderedRunable) {
			doExecute((OrderedRunable) task);
		} else {
			throw new RejectedExecutionException("task must be enclosed an OrderedRunable.");
		}
	}

	private void doExecute(OrderedRunable task) {
		getChildExecutor(task.key).execute(task);
	}

	private void doUnorderedExecute(ChildExecutor runnable) {
		super.execute(runnable);
	}

	private ChildExecutor getChildExecutor(Long key) {
		return childExecutors[(int) Math.abs(key % childExecutors.length)];
	}
	
	public ChildExecutor[] getChildExecutors() {
		return childExecutors;
	}



	/**
	 * Runable Task for OrderedThreadPoolExecutor
	 *
	 * @see net.moon.frame.utils.OrderedThreadPoolExecutor
	 */
	public abstract static class OrderedRunable implements Runnable {
		protected Long key;

		public OrderedRunable(Long key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return "OrderedRunable{" +
					"key=" + key +
					'}';
		}
	}

	/**
	 * 鐎圭偤妾幍褑顢戦懓锟�
	 */
	public final class ChildExecutor implements Executor, Runnable {
		private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
		private final AtomicBoolean isRunning = new AtomicBoolean();

		private final int executorId;
		private String name;

		public ChildExecutor(int executorId, String name) {
			this.executorId = executorId;
			this.name = name;
		}
		
		public int getExecutorId(){
			return executorId;
		}
		
		public String getName() {
			return name;
		}

		public int getQueueSize(){
			return tasks.size();
		}

		public void execute(Runnable command) {
			
			if(command == null){
				return;
			}
			
			// TODO: What todo if the add return false ?
			tasks.add(command);
//			logger.debug("add cmd " + command + " to ChildExecutor-" + executorId + " tasks.size=" + tasks.size() + " isRunning = " + isRunning);

			if(tasks.size() > 10){
				logger.info("閼版妞傞幈顢﹉ildExecutor闂冪喎鍨潻鍥с亣,size=" + tasks.size() + ",executorId=" + executorId+", name=" + name);
			}
			
			// add to schedule if executor is not waiting to process
			if (isRunning.compareAndSet(false, true)) {
//				logger.debug("into running " + command + " to ChildExecutor-" + executorId + " tasks.size=" + tasks.size());
				doUnorderedExecute(this);
			}
		}

		public void run() {

			try{

				int num = 0;
	
				while (true){
					final Runnable task = tasks.poll();
					// if the task is null we should exit the loop
					if (task == null) {
						break;
					}
	
	//				logger.debug("execute cmd start " + task + " in ChildExecutor-" + executorId + " tasks.size=" + tasks.size());
	//				long cTime = System.currentTimeMillis();
					boolean ran = false;
					try {
						task.run();
						ran = true;
					} catch (Exception e) {
						if (!ran) {
							logger.error("execute cmd " + task + " error:" + e.getMessage(), e);
						}
					}
	//				long nTime = System.currentTimeMillis();
	//				logger.debug("execute cmd " + task + " in ChildExecutor-" + executorId +" tasks.size="+tasks.size()+" time=" + (nTime-cTime));
					
					
					if(!isShutdown() && ++num > batchLimit && batchLimit > 0) {
						break;
					}
				}
				
			}catch(Exception ex){
				
				logger.error("ChildExecutor run 閸戞椽鏁婃禍鍡磼",ex);
				
			} finally {
				
//				logger.debug("ChildExecutor-" + executorId + " exit");
				
				isRunning.set(false);
				
				if (!tasks.isEmpty()) {
					if(isRunning.compareAndSet(false, true)){
						doUnorderedExecute(this);
					}
				}
				
				
				//閺冄冪杽閻滐拷(2015-1-15閺�锟�)
				// re-add to thread pool if tasks is not empty
//				if (!tasks.isEmpty()) {
//					doUnorderedExecute(this);
//				} else {
//					// set it back to not running
//					isRunning.set(false);
//					logger.debug("tasks.isEmpty() and isRunning set false");
//				}
				
			}

		}
	}
}

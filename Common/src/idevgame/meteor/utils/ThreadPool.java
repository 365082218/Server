package idevgame.meteor.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 缁捐法鈻煎Ч锟�
 * @author moon
 */
public class ThreadPool {
	private final static ExecutorService pool = Executors.newCachedThreadPool();
	private final static ScheduledExecutorService timer = Executors.newScheduledThreadPool(1*Runtime.getRuntime().availableProcessors());

	public static ExecutorService getPool() {
		return pool;
	}

	public static ScheduledExecutorService getTimer() {
		return timer;
	}
}

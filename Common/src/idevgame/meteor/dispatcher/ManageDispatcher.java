package idevgame.meteor.dispatcher;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 绠＄悊骞冲彴Dispatcher
 *
 * handler鍑芥暟鏍煎紡:
 * <pre>
 * &#064;ManageCMD(cmdId=xx)
 * public String xxx(String jsonObj) {...}
 * </pre>
 *
 * @author son
 * @version 2016骞�3鏈�2鏃�
 * @see net.moon.frame.dispatcher.ManageCMD
 */
public class ManageDispatcher {

	private static Logger logger = LoggerFactory.getLogger(ManageDispatcher.class);

	public static class Commander {
		private final Object o;
		private final Method method;

		public Commander(Object o, Method method) throws NoSuchMethodException {
			this.o = o;
			this.method = method;
		}

	}

	private Map<Integer, Commander> commanders = new HashMap<>();
	
	/**
	 * 鍗忚鍔犺浇
	 */
	public synchronized void load(Collection<Class> classes){

		Map<Integer, Commander> newCommanders = new HashMap<>();

		for (Class cls : classes) {

			try {
				Object o = cls.newInstance();
				Method[] methods = cls.getDeclaredMethods();

				for (Method method : methods) {
					ManageCMD cmd = method.getAnnotation(ManageCMD.class);
					if(cmd != null) {
						newCommanders.put(cmd.cmdId(), new Commander(o, method));
					}
				}
			} catch (Exception e) {
				logger.error("鍗忚["+cls+"]鍔犺浇鍑洪敊!!!", e);
			}

		}

		commanders = newCommanders;
	}

	/**
	 * 绠＄悊骞冲彴鐢�
	 * @param jsonObj
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public String invoke(int cmd, String jsonObj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
			long begin = System.currentTimeMillis();
			String res = (String) commander.method.invoke(commander.o, jsonObj);
			long used = System.currentTimeMillis() - begin;
			
			logger.debug("鍗忚[{}]澶勭悊瀹屾垚锛岃�楁椂{}ms", cmd, used);
			
			// 鍗忚澶勭悊瓒呰繃1绉�
			if (used > 1000) {
				logger.error("鍗忚[{}]澶勭悊鎱�!!!鑰楁椂{}ms", cmd, used);
			}
			
			return res;
		}
		return null;
	}

	public Commander getCommander(int cmd){
		return commanders.get(cmd);
	}
}

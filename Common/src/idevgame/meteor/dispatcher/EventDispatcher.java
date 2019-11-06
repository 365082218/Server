package idevgame.meteor.dispatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDispatcher {
	private static Logger logger = LoggerFactory.getLogger(EventDispatcher.class);
	public static class EventListener {
		private final Class type;
		private final Method method;
		public EventListener(Class t, Method method) throws NoSuchMethodException {
			this.type = t;
			this.method = method;
		}
	}

	private Map<Integer, List<EventListener>> listeners = new HashMap<>();
	public synchronized void load(Collection<Class> classes){
		Map<Integer, List<EventListener>> newCommanders = new HashMap<>();
		String err = null;
		for (Class cls : classes) {
			try {
				Method[] methods = cls.getDeclaredMethods();
				for (Method method : methods) {
					Event event = method.getAnnotation(Event.class);
					if(event != null) {
						if (newCommanders.get(event.id()) != null){
							List<EventListener> l = newCommanders.get(event.id());
							l.add(new EventListener(cls, method));
						}
						else
						{
							List<EventListener> l = new ArrayList<EventListener>();
							l.add(new EventListener(cls, method));
							newCommanders.put(event.id(), l);
						}
					}
				}
			} catch (Exception e) {
				logger.error("["+cls+"]!!!",e);
			}
		}
		listeners = newCommanders;
	}
	
	public void invoke(Object source, int cmd) throws Exception {
		List<EventListener> commander = listeners.get(cmd);
		if(commander != null) {
			for (int i = 0; i < commander.size(); i++)
			{
				EventListener l = commander.get(i);
				if (source.getClass() == l.type)
					l.method.invoke(source);
			}
		}
	}
	
	public List<EventListener> getListener(int cmd){
		return listeners.get(cmd);
	}
}

package idevgame.meteor.dispatcher;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
	private EventDispatcher eventDispatcher;
	public EventBus(EventDispatcher dispatcher)
	{
		eventDispatcher = dispatcher;
	}
	
	List<Object> eventObjects = new ArrayList<Object>();
	public void AddListener(Object clsInstance)
	{
		if (!eventObjects.contains(clsInstance))
			eventObjects.add(clsInstance);
	}
	
	public void RemoveListener(Object clsInstance)
	{
		if (eventObjects.contains(clsInstance))
			eventObjects.remove(clsInstance);
	}
	
	public void Fire(int id) 
	{
		try
		{
			for (int i = 0; i < eventObjects.size(); i++)
			{
				Object source = eventObjects.get(i);
				eventDispatcher.invoke(source, id);
			}
			
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}
	}
}

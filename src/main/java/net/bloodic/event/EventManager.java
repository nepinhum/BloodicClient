package net.bloodic.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import net.bloodic.BloodicClient;

public final class EventManager
{
	private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap =
		new HashMap<>();
	
	public EventManager()
	{
	}
	
	public static <L extends Listener, E extends Event<L>> void fire(E event)
	{
		BloodicClient client = BloodicClient.INSTANCE;
		if (client == null)
			return;
		
		EventManager eventManager = client.getEventManager();
		if (eventManager == null)
			return;
		
		eventManager.fireImpl(event);
	}
	
	private <L extends Listener, E extends Event<L>> void fireImpl(E event)
	{
		@SuppressWarnings("unchecked")
		ArrayList<L> listeners = (ArrayList<L>)listenerMap
			.get(event.getListenerType());
		
		if (listeners == null || listeners.isEmpty())
			return;
		
		ArrayList<L> listenersCopy = new ArrayList<>(listeners);
		listenersCopy.removeIf(Objects::isNull);
		
		event.fire(listenersCopy);
	}
	
	public <L extends Listener> void add(Class<L> type, L listener)
	{
		@SuppressWarnings("unchecked")
		ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(type);
		
		if (listeners == null) {
			listeners = new ArrayList<>(Arrays.asList(listener));
			listenerMap.put(type, listeners);
			return;
		}
		
		listeners.add(listener);
	}
	
	public <L extends Listener> void remove(Class<L> type, L listener)
	{
		@SuppressWarnings("unchecked")
		ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(type);
		
		if (listeners != null)
			listeners.remove(listener);
	}
}

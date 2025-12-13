package net.bloodic.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import net.bloodic.BloodicClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EventManager
{
	private static final Logger LOGGER = LogManager.getLogger("Bloodic/EventManager");

	private final HashMap<Class<? extends Listener>, ListenerBucket<? extends Listener>> listenerMap =
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
		ListenerBucket<L> bucket = (ListenerBucket<L>)listenerMap
			.get(event.getListenerType());

		if (bucket == null || bucket.isEmpty())
			return;

		Listener[] snapshot = bucket.snapshot();
		for (int i = 0; i < snapshot.length; i++) {
			@SuppressWarnings("unchecked")
			L listener = (L)snapshot[i];
			if (listener == null)
				continue;

			try {
				event.fire(listener);
			} catch (Throwable t) {
				LOGGER.error("Listener {} threw while handling {}", listener, event.getClass().getSimpleName(), t);
			}
		}
	}
	
	public <L extends Listener> void add(Class<L> type, L listener)
	{
		Objects.requireNonNull(listener, "listener");

		@SuppressWarnings("unchecked")
		ListenerBucket<L> bucket = (ListenerBucket<L>)listenerMap.get(type);

		if (bucket == null) {
			bucket = new ListenerBucket<>(listener);
			listenerMap.put(type, bucket);
		} else {
			bucket.add(listener);
		}
	}
	
	public <L extends Listener> void remove(Class<L> type, L listener)
	{
		if (listener == null)
			return;

		@SuppressWarnings("unchecked")
		ListenerBucket<L> bucket = (ListenerBucket<L>)listenerMap.get(type);
		if (bucket != null)
			bucket.remove(listener);
	}

	private static final class ListenerBucket<L extends Listener>
	{
		private final ArrayList<L> listeners;
		private volatile Listener[] snapshot;

		@SafeVarargs
		private ListenerBucket(L... first)
		{
			this.listeners = new ArrayList<>(Arrays.asList(first));
			rebuildSnapshot();
		}

		void add(L listener)
		{
			listeners.add(listener);
			rebuildSnapshot();
		}

		void remove(L listener)
		{
			if (listeners.remove(listener))
				rebuildSnapshot();
		}

		boolean isEmpty()
		{
			return snapshot.length == 0;
		}

		Listener[] snapshot()
		{
			return snapshot;
		}

		private void rebuildSnapshot()
		{
			List<L> clean = new ArrayList<>(listeners);
			clean.removeIf(Objects::isNull);
			snapshot = clean.toArray(new Listener[0]);
		}
	}
}

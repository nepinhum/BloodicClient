package net.bloodic.events;

import java.util.ArrayList;

import net.bloodic.event.Event;
import net.bloodic.event.Listener;

public interface UpdateListener extends Listener
{
	void onUpdate();
	
	final class UpdateEvent extends Event<UpdateListener>
	{
		public static final UpdateEvent INSTANCE = new UpdateEvent();
		
		@Override
		public void fire(ArrayList<UpdateListener> listeners)
		{
			for (UpdateListener listener : listeners)
				listener.onUpdate();
		}
		
		@Override
		public Class<UpdateListener> getListenerType()
		{
			return UpdateListener.class;
		}
	}
}

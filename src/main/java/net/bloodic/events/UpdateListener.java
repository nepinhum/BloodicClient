package net.bloodic.events;

import net.bloodic.event.Event;
import net.bloodic.event.Listener;

public interface UpdateListener extends Listener
{
	void onUpdate();
		
	final class UpdateEvent extends Event<UpdateListener>
	{
		public static final UpdateEvent INSTANCE = new UpdateEvent();
		
		@Override
		public Class<UpdateListener> getListenerType()
		{
			return UpdateListener.class;
		}

		@Override
		public void fire(UpdateListener listener)
		{
			listener.onUpdate();
		}
	}
}

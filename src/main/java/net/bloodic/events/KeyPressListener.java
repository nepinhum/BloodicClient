package net.bloodic.events;

import net.bloodic.event.Event;
import net.bloodic.event.Listener;

public interface KeyPressListener extends Listener
{
	void onKeyPress(KeyPressEvent event);
	
	final class KeyPressEvent extends Event<KeyPressListener>
	{
		private final int keyCode;
		private final int scanCode;
		private final int action;
		private final int modifiers;
		
		public KeyPressEvent(int keyCode, int scanCode, int action,
			int modifiers)
		{
			this.keyCode = keyCode;
			this.scanCode = scanCode;
			this.action = action;
			this.modifiers = modifiers;
		}
		
		@Override
		public Class<KeyPressListener> getListenerType()
		{
			return KeyPressListener.class;
		}

		@Override
		public void fire(KeyPressListener listener)
		{
			listener.onKeyPress(this);
		}
		
		public int getKeyCode()
		{
			return keyCode;
		}
		
		public int getScanCode()
		{
			return scanCode;
		}
		
		public int getAction()
		{
			return action;
		}
		
		public int getModifiers()
		{
			return modifiers;
		}
	}
}

package net.bloodic.events;

import net.bloodic.event.CancellableEvent;
import net.bloodic.event.Listener;

public interface ChatOutputListener extends Listener
{
	void onSentMessage(ChatOutputEvent event);

	final class ChatOutputEvent extends CancellableEvent<ChatOutputListener>
	{
		private String message;
		private final boolean command;

		public ChatOutputEvent(String message, boolean command)
		{
			this.message = message;
			this.command = command;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public boolean isCommand()
		{
			return command;
		}

		@Override
		public Class<ChatOutputListener> getListenerType()
		{
			return ChatOutputListener.class;
		}

		@Override
		public void fire(ChatOutputListener listener)
		{
			if (isCancelled())
				return;

			listener.onSentMessage(this);
		}
	}
}

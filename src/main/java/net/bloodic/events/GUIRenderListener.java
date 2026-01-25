package net.bloodic.events;

import net.bloodic.event.Event;
import net.bloodic.event.Listener;
import net.minecraft.client.gui.DrawContext;

public interface GUIRenderListener extends Listener
{
	void onRenderGUI(DrawContext context, float tickDelta);
		
	final class GUIRenderEvent extends Event<GUIRenderListener>
	{
		private final DrawContext context;
		private final float tickDelta;
		
		public GUIRenderEvent(DrawContext context, float tickDelta)
		{
			this.context = context;
			this.tickDelta = tickDelta;
		}
		
		@Override
		public Class<GUIRenderListener> getListenerType()
		{
			return GUIRenderListener.class;
		}

		@Override
		public void fire(GUIRenderListener listener)
		{
			listener.onRenderGUI(context, tickDelta);
		}
	}
}

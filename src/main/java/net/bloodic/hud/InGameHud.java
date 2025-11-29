package net.bloodic.hud;

import net.bloodic.event.EventManager;
import net.bloodic.events.GUIRenderListener;
import net.minecraft.client.gui.DrawContext;

public class InGameHud implements GUIRenderListener
{
	private final HackListHud listHud;
	
	public InGameHud(EventManager events)
	{
		listHud = new HackListHud();
		events.add(GUIRenderListener.class, this);
	}
	
	@Override
	public void onRenderGUI(DrawContext context, float tickDelta)
	{
		listHud.render(context, tickDelta);
	}
}

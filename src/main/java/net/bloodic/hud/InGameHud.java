package net.bloodic.hud;

import net.minecraft.client.gui.DrawContext;

public class InGameHud {
	private final HackListHud listHud;

	public InGameHud() {
		this.listHud = new HackListHud();
	}

	public void renderGUI(DrawContext context, float tickDelta) {
        listHud.render(context, tickDelta);
    }
}

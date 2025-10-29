package net.bloodic.hud;

import java.util.Comparator;
import java.util.List;

import net.bloodic.BloodicClient;
import net.bloodic.hack.Hack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class HackListHud {
	private final BloodicClient CLIENT = BloodicClient.INSTANCE;
	private final MinecraftClient MC = MinecraftClient.getInstance();

	public void render(DrawContext context, float tickDelta) {
		int index = 0;
		int screenWidth = MC.getWindow().getScaledWidth();
		TextRenderer textRenderer = MC.textRenderer;
		int fontHeight = textRenderer.fontHeight;

		List<Hack> enabledHacks = CLIENT.getHackManager().getEnabledHacks();
		enabledHacks.sort(Comparator.comparingInt(h -> MC.textRenderer.getWidth(((Hack) h).getName())).reversed());

		for (Hack hack : enabledHacks) {
			String name = hack.getName();
			int hackWidth = textRenderer.getWidth(name);
			int xPos = screenWidth - hackWidth - 4;
			int yPos = 10 + (index * fontHeight);

			context.drawTextWithShadow(textRenderer, name, xPos, yPos, 0xFFFFFF);
			index++;
		}
	}
}

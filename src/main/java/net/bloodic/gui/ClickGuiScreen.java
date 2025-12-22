package net.bloodic.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.bloodic.BloodicClient;
import net.bloodic.hack.Hack;
import net.bloodic.settings.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen
{
	private final BloodicClient client = BloodicClient.INSTANCE;
	private final Map<Hack.Category, Boolean> expanded = new EnumMap<>(Hack.Category.class);
	private final Map<Hack, Boolean> hackSettingsExpanded = new HashMap<>();
	private Map<Hack.Category, List<Hack>> grouped;
	
	private static final int LEFT_MARGIN = 20;
	private static final int TOP_MARGIN = 20;
	private static final int PANEL_WIDTH = 260;
	private static final int HEADER_HEIGHT = 18;
	private static final int HACK_ROW_HEIGHT = 28;
	private static final int SETTING_ROW_HEIGHT = 22;
	private static final int SETTING_INDENT = 12;
	private static final int VERTICAL_GAP = 8;
	private static final int BUTTON_ROW_HEIGHT = 24;
	
	public ClickGuiScreen()
	{
		super(Text.literal("Bloodic Click GUI"));
		for (Hack.Category category : Hack.Category.values()) {
			expanded.put(category, true);
		}
	}
	
	@Override
	protected void init()
	{
		super.init();
		grouped = groupHacks();
		
		int buttonY = 6;
		int buttonWidth = 70;
		int spacing = 4;
		int startX = this.width - (buttonWidth * 2 + spacing + LEFT_MARGIN);
		
		addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
			client.getConfigManager().saveAsync();
		}).dimensions(startX, buttonY, buttonWidth, 18).build());
		
		addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
			client.getConfigManager().reset();
		}).dimensions(startX + (buttonWidth + spacing), buttonY, buttonWidth, 18).build());
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context);

		int y = TOP_MARGIN + BUTTON_ROW_HEIGHT;
		for (Hack.Category category : Hack.Category.values()) {
			List<Hack> hacks = grouped.getOrDefault(category, List.of());
			if (hacks.isEmpty())
				continue;
			
			boolean isExpanded = expanded.getOrDefault(category, true);
			
			drawCategoryHeader(context, category, y, isExpanded, mouseX, mouseY);
			y += HEADER_HEIGHT;
			
			if (isExpanded) {
				for (Hack hack : hacks) {
					drawHackRow(context, hack, y, mouseX, mouseY);
					y += HACK_ROW_HEIGHT;

					boolean settingsOpen = hackSettingsExpanded.getOrDefault(hack, false);
					List<Setting<?>> settings = hack.getSettings();
					if (settingsOpen && !settings.isEmpty()) {
						for (Setting<?> setting : settings) {
							drawSettingRow(context, setting, y, mouseX, mouseY);
							y += SETTING_ROW_HEIGHT;
						}
					}
				}
			}
			
			y += VERTICAL_GAP;
		}
		
		super.render(context, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (button != 0 && button != 1)
			return super.mouseClicked(mouseX, mouseY, button);

		int y = TOP_MARGIN + BUTTON_ROW_HEIGHT;
		for (Hack.Category category : Hack.Category.values()) {
			List<Hack> hacks = grouped.getOrDefault(category, List.of());
			if (hacks.isEmpty())
				continue;
			
			if (isWithin(mouseX, mouseY, LEFT_MARGIN, y, PANEL_WIDTH, HEADER_HEIGHT)) {
				expanded.put(category, !expanded.getOrDefault(category, true));
				return true;
			}
			
			y += HEADER_HEIGHT;
			
			if (expanded.getOrDefault(category, true)) {
				for (Hack hack : hacks) {
					boolean hackRow = isWithin(mouseX, mouseY, LEFT_MARGIN, y, PANEL_WIDTH, HACK_ROW_HEIGHT);
					if (hackRow && button == 0) {
						hack.toggle();
						return true;
					}
					if (hackRow && button == 1 && !hack.getSettings().isEmpty()) {
						hackSettingsExpanded.put(hack, !hackSettingsExpanded.getOrDefault(hack, false));
						return true;
					}
					y += HACK_ROW_HEIGHT;

					boolean settingsOpen = hackSettingsExpanded.getOrDefault(hack, false);
					if (settingsOpen && !hack.getSettings().isEmpty()) {
						for (Setting<?> setting : hack.getSettings()) {
							if (isWithin(mouseX, mouseY, LEFT_MARGIN + SETTING_INDENT, y, PANEL_WIDTH - SETTING_INDENT, SETTING_ROW_HEIGHT)) {
								handleSettingClick(setting, button);
								return true;
							}
							y += SETTING_ROW_HEIGHT;
						}
					}
				}
			}
			
			y += VERTICAL_GAP;
		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean shouldPause()
	{
		return false;
	}
	
	private void drawCategoryHeader(DrawContext context, Hack.Category category, int y, boolean isExpanded, int mouseX, int mouseY)
	{
		int color = isWithin(mouseX, mouseY, LEFT_MARGIN, y, PANEL_WIDTH, HEADER_HEIGHT) ? 0xFF2E2E2E : 0xFF1E1E1E;
		context.fill(LEFT_MARGIN, y, LEFT_MARGIN + PANEL_WIDTH, y + HEADER_HEIGHT, color);
		
		String prefix = isExpanded ? "▼ " : "▶ ";
		context.drawTextWithShadow(textRenderer, prefix + category.name, LEFT_MARGIN + 6, y + 4, 0xFFFFFF);
	}
	
	private void drawHackRow(DrawContext context, Hack hack, int y, int mouseX, int mouseY)
	{
		boolean hovered = isWithin(mouseX, mouseY, LEFT_MARGIN, y, PANEL_WIDTH, HACK_ROW_HEIGHT);
		int baseColor = hovered ? 0xFF2E2E2E : 0xFF262626;
		context.fill(LEFT_MARGIN, y, LEFT_MARGIN + PANEL_WIDTH, y + HACK_ROW_HEIGHT, baseColor);
		
		int nameColor = hack.isEnabled() ? 0x55FF55 : 0xFFFFFF;
		context.drawTextWithShadow(textRenderer, hack.getName(), LEFT_MARGIN + 6, y + 4, nameColor);
		
		String keyInfo = "Key: " + getKeyName(hack.getKey());
		context.drawTextWithShadow(textRenderer, keyInfo, LEFT_MARGIN + 140, y + 4, 0xAAAAAA);
		
		String description = hack.getDescription();
		if (description != null && !description.isEmpty()) {
			context.drawTextWithShadow(textRenderer, description, LEFT_MARGIN + 6, y + 16, 0xCCCCCC);
		}
	}

	private void drawSettingRow(DrawContext context, Setting<?> setting, int y, int mouseX, int mouseY)
	{
		boolean hovered = isWithin(mouseX, mouseY, LEFT_MARGIN + SETTING_INDENT, y, PANEL_WIDTH - SETTING_INDENT, SETTING_ROW_HEIGHT);
		int baseColor = hovered ? 0xFF1E1E1E : 0xFF181818;
		context.fill(LEFT_MARGIN + SETTING_INDENT, y, LEFT_MARGIN + PANEL_WIDTH, y + SETTING_ROW_HEIGHT, baseColor);

		context.drawTextWithShadow(textRenderer, setting.getName(), LEFT_MARGIN + SETTING_INDENT + 6, y + 4, 0xFFFFFF);
		context.drawTextWithShadow(textRenderer, setting.getDisplayValue(), LEFT_MARGIN + PANEL_WIDTH - 70, y + 4, 0xAAAAAA);

		String description = setting.getDescription();
		if (description != null && !description.isEmpty()) {
			context.drawTextWithShadow(textRenderer, description, LEFT_MARGIN + SETTING_INDENT + 6, y + 12, 0x777777);
		}
	}

	private void handleSettingClick(Setting<?> setting, int button)
	{
		if (button == 0) {
			setting.next();
		} else if (button == 1) {
			setting.previous();
		}
		client.getConfigManager().saveAsync();
	}
	
	private Map<Hack.Category, List<Hack>> groupHacks()
	{
		Map<Hack.Category, List<Hack>> map = new EnumMap<>(Hack.Category.class);
		
		for (Hack hack : client.getHackManager().getHacks()) {
			map.computeIfAbsent(hack.getCategory(), c -> new ArrayList<>()).add(hack);
		}
		
		return map;
	}
	
	private boolean isWithin(double mouseX, double mouseY, int x, int y, int width, int height)
	{
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	private String getKeyName(int keyCode)
	{
		String glfwName = GLFW.glfwGetKeyName(keyCode, 0);
		if (glfwName != null && !glfwName.isEmpty())
			return glfwName.toUpperCase();
		
		return "Key " + keyCode;
	}
}

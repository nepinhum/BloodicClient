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
	private final Map<Hack.Category, PanelState> panels = new EnumMap<>(Hack.Category.class);
	private Map<Hack.Category, List<Hack>> grouped;
	private Hack.Category draggingCategory;
	private int dragOffsetX;
	private int dragOffsetY;
	private boolean draggingMoved;

	private static final int LEFT_MARGIN = 20;
	private static final int TOP_MARGIN = 20;
	private static final int PANEL_WIDTH = 210;
	private static final int HEADER_HEIGHT = 14;
	private static final int HACK_ROW_HEIGHT = 20;
	private static final int SETTING_ROW_HEIGHT = 18;
	private static final int SETTING_INDENT = 10;
	private static final int VERTICAL_GAP = 8;
	private static final int BUTTON_ROW_HEIGHT = 20;

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
		initPanels();

		int buttonY = 6;
		int buttonWidth = 64;
		int spacing = 4;
		int startX = this.width - (buttonWidth * 2 + spacing + LEFT_MARGIN);

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.buttons.save"), b -> {
			client.getConfigManager().saveAsync();
		}).dimensions(startX, buttonY, buttonWidth, 18).build());

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.buttons.reset"), b -> {
			client.getConfigManager().reset();
		}).dimensions(startX + (buttonWidth + spacing), buttonY, buttonWidth, 18).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context, mouseX, mouseY, delta);

		for (Hack.Category category : Hack.Category.values()) {
			List<Hack> hacks = grouped.getOrDefault(category, List.of());
			boolean isExpanded = expanded.getOrDefault(category, true);

			PanelState panel = panels.get(category);
			int panelX = panel != null ? panel.x : LEFT_MARGIN;
			int panelY = panel != null ? panel.y : TOP_MARGIN + BUTTON_ROW_HEIGHT;

			drawCategoryHeader(context, category, panelX, panelY, isExpanded, mouseX, mouseY);
			int y = panelY + HEADER_HEIGHT;

			if (isExpanded && !hacks.isEmpty()) {
				for (Hack hack : hacks) {
					drawHackRow(context, hack, panelX, y, mouseX, mouseY);
					y += HACK_ROW_HEIGHT;

					boolean settingsOpen = hackSettingsExpanded.getOrDefault(hack, false);
					List<Setting<?>> settings = hack.getSettings();
					if (settingsOpen && !settings.isEmpty()) {
						for (Setting<?> setting : settings) {
							drawSettingRow(context, setting, panelX, y, mouseX, mouseY);
							y += SETTING_ROW_HEIGHT;
						}
					}
				}
			}
		}

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (button != 0 && button != 1)
			return super.mouseClicked(mouseX, mouseY, button);

		for (Hack.Category category : Hack.Category.values()) {
			List<Hack> hacks = grouped.getOrDefault(category, List.of());

			PanelState panel = panels.get(category);
			int panelX = panel != null ? panel.x : LEFT_MARGIN;
			int panelY = panel != null ? panel.y : TOP_MARGIN + BUTTON_ROW_HEIGHT;

			if (isWithin(mouseX, mouseY, panelX, panelY, PANEL_WIDTH, HEADER_HEIGHT)) {
				if (button == 0) {
					startDrag(category, (int)mouseX, (int)mouseY, panelX, panelY);
				}
				return true;
			}

			int y = panelY + HEADER_HEIGHT;

			if (expanded.getOrDefault(category, true) && !hacks.isEmpty()) {
				for (Hack hack : hacks) {
					boolean hackRow = isWithin(mouseX, mouseY, panelX, y, PANEL_WIDTH, HACK_ROW_HEIGHT);
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
							if (isWithin(mouseX, mouseY, panelX + SETTING_INDENT, y, PANEL_WIDTH - SETTING_INDENT, SETTING_ROW_HEIGHT)) {
								handleSettingClick(setting, button);
								return true;
							}
							y += SETTING_ROW_HEIGHT;
						}
					}
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (draggingCategory != null && button == 0) {
			PanelState panel = panels.get(draggingCategory);
			if (panel != null) {
				panel.x = (int)mouseX - dragOffsetX;
				panel.y = (int)mouseY - dragOffsetY;
				if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1)
					draggingMoved = true;
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (draggingCategory != null && button == 0) {
			if (!draggingMoved) {
				Hack.Category category = draggingCategory;
				expanded.put(category, !expanded.getOrDefault(category, true));
			}
			draggingCategory = null;
			draggingMoved = false;
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean shouldPause()
	{
		return false;
	}

	private void drawCategoryHeader(DrawContext context, Hack.Category category, int x, int y, boolean isExpanded, int mouseX, int mouseY)
	{
		int color = isWithin(mouseX, mouseY, x, y, PANEL_WIDTH, HEADER_HEIGHT) ? 0xFF2E2E2E : 0xFF1E1E1E;
		context.fill(x, y, x + PANEL_WIDTH, y + HEADER_HEIGHT, color);

		String prefix = isExpanded ? "▼ " : "▶ ";
		context.drawTextWithShadow(textRenderer, prefix + category.name, x + 5, y + 2, 0xFFFFFF);
	}

	private void drawHackRow(DrawContext context, Hack hack, int x, int y, int mouseX, int mouseY)
	{
		boolean hovered = isWithin(mouseX, mouseY, x, y, PANEL_WIDTH, HACK_ROW_HEIGHT);
		int baseColor = hovered ? 0xFF2E2E2E : 0xFF262626;
		context.fill(x, y, x + PANEL_WIDTH, y + HACK_ROW_HEIGHT, baseColor);

		int nameColor = hack.isEnabled() ? 0x55FF55 : 0xFFFFFF;
		context.drawTextWithShadow(textRenderer, hack.getName(), x + 5, y + 3, nameColor);

		Text keyInfo = Text.translatable(
				"gui.texts.key",
				getKeyName(hack.getKey())
		);
		context.drawTextWithShadow(textRenderer, keyInfo, x + 115, y + 3, 0xAAAAAA);

		String descriptionKey = hack.getDescription();
		if (descriptionKey != null && !descriptionKey.isEmpty()) {
			context.drawTextWithShadow(textRenderer, Text.translatable(descriptionKey), x + 5, y + 12, 0xCCCCCC);
		}
	}

	private void drawSettingRow(DrawContext context, Setting<?> setting, int x, int y, int mouseX, int mouseY)
	{
		boolean hovered = isWithin(mouseX, mouseY, x + SETTING_INDENT, y, PANEL_WIDTH - SETTING_INDENT, SETTING_ROW_HEIGHT);
		int baseColor = hovered ? 0xFF1E1E1E : 0xFF181818;
		context.fill(x + SETTING_INDENT, y, x + PANEL_WIDTH, y + SETTING_ROW_HEIGHT, baseColor);

		context.drawTextWithShadow(textRenderer, setting.getName(), x + SETTING_INDENT + 5, y + 3, 0xFFFFFF);
		context.drawTextWithShadow(textRenderer, setting.getDisplayValue(), x + PANEL_WIDTH - 60, y + 3, 0xAAAAAA);

		String description = setting.getDescription();
		if (description != null && !description.isEmpty()) {
			context.drawTextWithShadow(textRenderer, description, x + SETTING_INDENT + 5, y + 10, 0x777777);
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

	private void initPanels()
	{
		int x = LEFT_MARGIN;
		int y = TOP_MARGIN + BUTTON_ROW_HEIGHT;
		int maxY = this.height - TOP_MARGIN;

		for (Hack.Category category : Hack.Category.values()) {
			if (!panels.containsKey(category)) {
				List<Hack> hacks = grouped.getOrDefault(category, List.of());
				int panelHeight = HEADER_HEIGHT + (hacks.size() * HACK_ROW_HEIGHT) + VERTICAL_GAP;
				if (y + panelHeight > maxY) {
					x += PANEL_WIDTH + VERTICAL_GAP;
					y = TOP_MARGIN + BUTTON_ROW_HEIGHT;
				}

				panels.put(category, new PanelState(x, y));
				y += panelHeight;
			}
		}
	}

	private void startDrag(Hack.Category category, int mouseX, int mouseY, int panelX, int panelY)
	{
		draggingCategory = category;
		dragOffsetX = mouseX - panelX;
		dragOffsetY = mouseY - panelY;
		draggingMoved = false;
	}

	private static final class PanelState
	{
		private int x;
		private int y;

		private PanelState(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	@Override
	protected void applyBlur(float delta) {
		// NOOP
	}
}

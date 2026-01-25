package net.bloodic.gui;

import java.util.ArrayList;
import java.util.List;

import net.bloodic.BloodicClient;
import net.bloodic.hack.Hack;
import net.bloodic.keybinds.Keybind;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindScreen extends Screen
{
	private static final int ROW_HEIGHT = 20;
	private static final int LIST_TOP = 40;
	private static final int LIST_BOTTOM_PADDING = 34;
	private static final int KEY_AREA_WIDTH = 72;

	private final BloodicClient client = BloodicClient.INSTANCE;
	private final Screen parent;
	private TextFieldWidget searchBox;
	private List<Hack> filtered = List.of();
	private int scrollOffset;
	private Hack listeningForKey;

	public KeybindScreen(Screen parent)
	{
		super(Text.translatable("gui.keybinds.title"));
		this.parent = parent;
	}

	@Override
	protected void init()
	{
		/*int searchWidth = Math.min(220, this.width - 40);
		int searchX = (this.width - searchWidth) / 2;
		searchBox = new TextFieldWidget(textRenderer, searchX, 16, searchWidth, 16,
			Text.translatable("gui.keybinds.search"));
		searchBox.setChangedListener(value -> updateFilter());
		addDrawableChild(searchBox);*/

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.buttons.reset_all"), b -> {
			for (Hack hack : client.getHackManager().getHacks()) {
				hack.setKey(Keybind.findPossibleKeybind(hack));
			}
		}).dimensions(10, this.height - 24, 90, 18).build());

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.buttons.done"), b -> {
			BloodicClient.MC.setScreen(parent);
		}).dimensions(this.width - 90, this.height - 24, 80, 18).build());

		updateFilter();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 4, 0xFFFFFF);

		if (listeningForKey != null) {
			Text hint = Text.translatable("gui.texts.bind_key");
			int hintWidth = textRenderer.getWidth(hint);
			context.drawTextWithShadow(textRenderer, hint, (this.width - hintWidth) / 2, this.height - 52, 0xFFFFFF);
		}

		int listLeft = 10;
		int listRight = this.width - 10;
		int listBottom = this.height - LIST_BOTTOM_PADDING;
        context.fill(listLeft, LIST_TOP, listRight, listBottom, 0xAA000000);
		for (int i = 0; i < filtered.size(); i++) {
			int rowY = LIST_TOP + (i * ROW_HEIGHT) - scrollOffset;
			if (rowY + ROW_HEIGHT < LIST_TOP || rowY > listBottom)
				continue;

			Hack hack = filtered.get(i);
			boolean hovered = isWithin(mouseX, mouseY, listLeft, rowY, listRight - listLeft);
			int color = hovered ? 0xFF262626 : 0xFF1E1E1E;
			context.fill(listLeft, rowY, listRight, rowY + ROW_HEIGHT, color);

			context.drawTextWithShadow(textRenderer, hack.getName(), listLeft + 6, rowY + 6, 0xFFFFFF);

			String keyName = listeningForKey == hack ? "..." : getKeyName(hack.getKey());
			Text keyText = Text.translatable("gui.texts.key", keyName);
			int keyX = listRight - KEY_AREA_WIDTH;
			context.drawTextWithShadow(textRenderer, keyText, keyX + 6, rowY + 6, 0xAAAAAA);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (searchBox != null && searchBox.mouseClicked(mouseX, mouseY, button))
			return true;

		int listLeft = 10;
		int listRight = this.width - 10;
		int listBottom = this.height - LIST_BOTTOM_PADDING;
		for (int i = 0; i < filtered.size(); i++) {
			int rowY = LIST_TOP + (i * ROW_HEIGHT) - scrollOffset;
			if (rowY + ROW_HEIGHT < LIST_TOP || rowY > listBottom)
				continue;

			int keyX = listRight - KEY_AREA_WIDTH;
			if (isWithin(mouseX, mouseY, keyX, rowY, KEY_AREA_WIDTH)) {
				Hack hack = filtered.get(i);
				if (button == 1) {
					hack.setKey(0);
					return true;
				}
				if (button == 0) {
					listeningForKey = hack;
					return true;
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
	{
		int listBottom = this.height - LIST_BOTTOM_PADDING;
		int listHeight = listBottom - LIST_TOP;
		int totalHeight = filtered.size() * ROW_HEIGHT;
		int maxScroll = Math.max(0, totalHeight - listHeight);

		scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)(verticalAmount * 12)));
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (listeningForKey != null) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				listeningForKey = null;
				return true;
			}
			if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
				listeningForKey.setKey(0);
				listeningForKey = null;
				return true;
			}

			listeningForKey.setKey(keyCode);
			listeningForKey = null;
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers)
	{
		if (searchBox != null && searchBox.charTyped(chr, modifiers))
			return true;

		return super.charTyped(chr, modifiers);
	}

	@Override
	public void removed()
	{
		client.getConfigManager().saveAsync();
		super.removed();
	}

	private void updateFilter()
	{
		String query = searchBox != null ? searchBox.getText().trim().toLowerCase() : "";
		List<Hack> all = client.getHackManager().getHacks();
		if (query.isEmpty()) {
			filtered = all;
		} else {
			List<Hack> matches = new ArrayList<>();
			for (Hack hack : all) {
				if (hack.getName().toLowerCase().contains(query))
					matches.add(hack);
			}
			filtered = matches;
		}
		scrollOffset = 0;
	}

	private boolean isWithin(double mouseX, double mouseY, int x, int y, int width)
	{
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + KeybindScreen.ROW_HEIGHT;
	}

	private String getKeyName(int keyCode)
	{
		if (keyCode == 0)
			return "None";

		String glfwName = GLFW.glfwGetKeyName(keyCode, 0);
		if (glfwName != null && !glfwName.isEmpty())
			return glfwName.toUpperCase();

		return "Key " + keyCode;
	}

	@Override
	protected void applyBlur(float delta) {
		// NOOP
	}
}

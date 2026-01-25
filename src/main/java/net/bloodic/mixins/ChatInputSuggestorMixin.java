package net.bloodic.mixins;

import java.util.List;
import java.util.stream.Collectors;

import net.bloodic.BloodicClient;
import net.bloodic.command.CmdProcessor;
import net.bloodic.registry.impl.CommandRegistry;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin
{
	@Unique
	private List<String> bloodic$matches = List.of();
	@Unique
	private int bloodic$matchIndex;
	@Unique
	private String bloodic$lastPrefix = "";

	@Shadow
	private TextFieldWidget textField;

	@Shadow

	@Inject(method = "refresh", at = @At("TAIL"))
	private void bloodic$onRefresh(CallbackInfo ci)
	{
		if (textField == null)
			return;

		String text = textField.getText();
		if (text == null || !text.startsWith(CmdProcessor.DOT_PREFIX)) {
			textField.setSuggestion(null);
			return;
		}

		String raw = text.substring(CmdProcessor.DOT_PREFIX.length());
		int space = raw.indexOf(' ');
		if (space != -1) {
			textField.setSuggestion(null);
			return;
		}

		String prefix = raw.toLowerCase();
		if (!prefix.equals(bloodic$lastPrefix)) {
			bloodic$matches = getMatches(prefix);
			bloodic$matchIndex = 0;
			bloodic$lastPrefix = prefix;
		}

		if (bloodic$matches.isEmpty()) {
			textField.setSuggestion(null);
			return;
		}

		String next = bloodic$matches.get(0);
		String suffix = next.substring(prefix.length());
		textField.setSuggestion(suffix);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void bloodic$onKeyPressed(int keyCode, int scanCode, int modifiers,
		CallbackInfoReturnable<Boolean> cir)
	{
		if (keyCode != GLFW.GLFW_KEY_TAB || textField == null)
			return;

		String text = textField.getText();
		if (text == null || !text.startsWith(CmdProcessor.DOT_PREFIX))
			return;

		String raw = text.substring(CmdProcessor.DOT_PREFIX.length());
		int space = raw.indexOf(' ');
		String prefix = space == -1 ? raw : raw.substring(0, space);
		String rest = space == -1 ? "" : raw.substring(space);
		if (prefix.isEmpty())
			return;

		String lower = prefix.toLowerCase();
		if (!lower.equals(bloodic$lastPrefix)) {
			bloodic$matches = getMatches(lower);
			bloodic$matchIndex = 0;
			bloodic$lastPrefix = lower;
		}

		if (bloodic$matches.isEmpty())
			return;

		String next = bloodic$matches.get(bloodic$matchIndex % bloodic$matches.size());
		bloodic$matchIndex = (bloodic$matchIndex + 1) % bloodic$matches.size();

		textField.setText(CmdProcessor.DOT_PREFIX + next + rest);
		textField.setCursorToEnd(false);
		textField.setSuggestion(null);
		cir.setReturnValue(true);
	}

	private List<String> getMatches(String prefix)
	{
		CommandRegistry registry = BloodicClient.INSTANCE != null
			? BloodicClient.INSTANCE.getCommandRegistry()
			: null;
		if (registry == null)
			return List.of();

		return registry.getAll().keySet().stream()
			.filter(cmd -> cmd.startsWith(prefix))
			.sorted()
			.collect(Collectors.toList());
	}
}

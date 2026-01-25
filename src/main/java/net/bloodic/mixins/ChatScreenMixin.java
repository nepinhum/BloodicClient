package net.bloodic.mixins;

import net.bloodic.event.EventManager;
import net.bloodic.events.ChatOutputListener.ChatOutputEvent;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin
{
	@Unique
	private boolean bloodic$forwarding;

	@Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
	private void bloodic$onSendMessage(String message, boolean addToHistory, CallbackInfo ci)
	{
		if (bloodic$forwarding)
			return;

		boolean command = message != null && message.startsWith("/");
		ChatOutputEvent event = new ChatOutputEvent(message, command);
		EventManager.fire(event);

		if (event.isCancelled()) {
			ci.cancel();
			return;
		}

		String updated = event.getMessage();
		if (updated != null && !updated.equals(message)) {
			bloodic$forwarding = true;
			((ChatScreen)(Object)this).sendMessage(updated, addToHistory);
			bloodic$forwarding = false;
			ci.cancel();
		}
	}

}

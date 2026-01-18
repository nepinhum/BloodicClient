package net.bloodic.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.bloodic.event.EventManager;
import net.bloodic.events.KeyPressListener.KeyPressEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(Keyboard.class)
public class KeyboardMixin
{
	@Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
	public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen)
			return;

		EventManager.fire(new KeyPressEvent(key, scancode, action, modifiers));
	}
}

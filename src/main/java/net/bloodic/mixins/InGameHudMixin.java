package net.bloodic.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.bloodic.event.EventManager;
import net.bloodic.events.GUIRenderListener.GUIRenderEvent;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
	@Inject(method = "render", at = @At("RETURN"))
	private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		float tickDelta = tickCounter.getTickDelta(false);
		EventManager.fire(new GUIRenderEvent(context, tickDelta));
	}
}

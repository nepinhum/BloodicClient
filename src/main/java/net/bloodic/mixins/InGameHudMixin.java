	package net.bloodic.mixins;

	import net.minecraft.client.gui.DrawContext;
	import org.spongepowered.asm.mixin.Mixin;
	import org.spongepowered.asm.mixin.injection.At;
	import org.spongepowered.asm.mixin.injection.Inject;
	import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

	import net.bloodic.BloodicClient;
	import net.minecraft.client.gui.hud.InGameHud;

	@Mixin(InGameHud.class)
	public class InGameHudMixin{
		@Inject(method = "render", at = @At("RETURN"))
		private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
			BloodicClient.INSTANCE.onRenderGUI(context, tickDelta);
		}
	}

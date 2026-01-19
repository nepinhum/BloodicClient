package net.bloodic.hacks;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.BooleanSetting;
import net.bloodic.utils.AntiKick;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Flight extends Hack implements UpdateListener
{
	private final BooleanSetting antiKick;
	private final BooleanSetting noFall;
	private final AntiKick antiKickHelper = new AntiKick(20, -0.04);

	public Flight()
	{
		super("Flight", "hacks.descs.flight", Category.MOVEMENT);
		antiKick = addSetting(new BooleanSetting("AntiKick", "Small downward pulses while hovering.", true));
		noFall = addSetting(new BooleanSetting("NoFall", "Reset fall distance while flying.", true));
	}
	
	@Override
	protected void onEnable()
	{
		CL.getHackManager().getHackByName("CreativeFlight").setEnabled(false);
		events().add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		if (player != null) {
			player.getAbilities().flying = false;
			if (noFall.getValue())
				player.fallDistance = 0;

			Vec3d velocity = player.getVelocity();
			double y = 0;
			if (MC.options.jumpKey.isPressed())
				y = 1;

			if (MC.options.sneakKey.isPressed())
				y = -1;

			player.setVelocity(velocity.x, y, velocity.z);
			antiKickHelper.tick(player, antiKick.getValue() && shouldAntiKick(player));
		}
	}

	private boolean shouldAntiKick(ClientPlayerEntity player)
	{
		if (MC.options.jumpKey.isPressed() || MC.options.sneakKey.isPressed())
			return false;

		if (player.input == null)
			return true;

		return player.input.movementForward == 0 && player.input.movementSideways == 0;
	}
	
	@Override
	protected void onDisable()
	{
		events().remove(UpdateListener.class, this);
		antiKickHelper.reset();
	}
}

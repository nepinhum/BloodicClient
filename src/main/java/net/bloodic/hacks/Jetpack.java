package net.bloodic.hacks;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.BooleanSetting;
import net.bloodic.settings.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Jetpack extends Hack implements UpdateListener
{
	private final NumberSetting thrust;
	private final BooleanSetting noFall;

	public Jetpack()
	{
		super("Jetpack", "hacks.descs.jetpack", Category.MOVEMENT);
		thrust = addSetting(new NumberSetting("Thrust", "Up/down thrust speed.", 0.35, 0.10, 1.00, 0.05));
		noFall = addSetting(new BooleanSetting("NoFall", "Reset fall distance while thrusting.", true));
	}

	@Override
	protected void onEnable()
	{
		CL.getHackManager().getHackByName("Flight").setEnabled(false);
		CL.getHackManager().getHackByName("CreativeFlight").setEnabled(false);
		events().add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		events().remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		if (player == null)
			return;

		boolean up = MC.options.jumpKey.isPressed();
		boolean down = MC.options.sneakKey.isPressed();
		if (!up && !down)
			return;

		double y = up ? thrust.getValue() : -thrust.getValue();
		Vec3d velocity = player.getVelocity();
		player.setVelocity(velocity.x, y, velocity.z);
		if (noFall.getValue())
			player.fallDistance = 0;
	}
}

package net.bloodic.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
// TODO This method sucks rn, will be safer in the future (and you'll probably get kicked with current one)
public class Flight extends Hack implements UpdateListener
{
	public Flight()
	{
		super("Flight", "hacks.descs.flight", Category.MOVEMENT, GLFW.GLFW_KEY_F);
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
			Vec3d velocity = player.getVelocity();
			double y = 0;
			if (MC.options.jumpKey.isPressed())
				y = 1;

			if (MC.options.sneakKey.isPressed())
				y = -1;

			player.setVelocity(velocity.x, y, velocity.z);
		}
	}
	
	@Override
	protected void onDisable()
	{
		events().remove(UpdateListener.class, this);
	}
}

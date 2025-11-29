package net.bloodic.hacks;

import org.lwjgl.glfw.GLFW;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;

public class Flight extends Hack implements UpdateListener
{
	
	public Flight()
	{
		super("Flight", "Allows to fly.", Hack.Category.MOVEMENT, GLFW.GLFW_KEY_F);
	}
	
	@Override
	protected void onEnable()
	{
		events().add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// well, for test
		if (MC.player != null) {
			MC.player.getAbilities().allowFlying = true;
			MC.player.getAbilities().flying = true;
		}
	}
	
	@Override
	protected void onDisable()
	{
		events().remove(UpdateListener.class, this);
        if (MC.player != null) {
            MC.player.getAbilities().allowFlying = false;
			MC.player.getAbilities().flying = false;
        }
	}
}

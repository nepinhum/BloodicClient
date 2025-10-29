package net.bloodic.hacks;

import org.lwjgl.glfw.GLFW;

import net.bloodic.hack.Hack;

public class Flight extends Hack{
	
	public Flight(){
		super("Flight", "Allows to fly.", Hack.Category.MOVEMENT, GLFW.GLFW_KEY_F);
	}
	
	@Override
	public void onUpdate(){
		// well, for test
		if (MC.player != null) {
			MC.player.getAbilities().allowFlying = true;
			MC.player.getAbilities().flying = true;
		}
	}
	
	@Override
	protected void onDisable(){
        if (MC.player != null) {
            MC.player.getAbilities().allowFlying = false;
			MC.player.getAbilities().flying = false;
        }
	}
}

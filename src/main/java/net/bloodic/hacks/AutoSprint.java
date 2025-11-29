package net.bloodic.hacks;

import org.lwjgl.glfw.GLFW;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;

public class AutoSprint extends Hack implements UpdateListener
{
	
	public AutoSprint()
	{
		super("AutoSprint", "Keeps your sprint.", Hack.Category.MOVEMENT, GLFW.GLFW_KEY_V); // V
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
			MC.player.setSprinting(true);
		}
	}
	
	@Override
	protected void onDisable()
	{
		events().remove(UpdateListener.class, this);
		if (MC.player != null) {
			MC.player.setSprinting(false);
		}
	}
}

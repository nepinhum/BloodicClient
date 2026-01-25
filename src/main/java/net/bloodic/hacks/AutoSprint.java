package net.bloodic.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.BooleanSetting;

public class AutoSprint extends Hack implements UpdateListener
{
	private final BooleanSetting forwardOnly;
	
	public AutoSprint()
	{
		super("AutoSprint", "hacks.descs.autosprint", Category.MOVEMENT);
		forwardOnly = addSetting(new BooleanSetting("Forward Only", "Sprint only while moving forward.", true));
	}
	
	@Override
	protected void onEnable()
	{
		events().add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		if (player != null) {
			if (!forwardOnly.getValue() || MC.options.forwardKey.isPressed()) {
				player.setSprinting(true);
			}
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

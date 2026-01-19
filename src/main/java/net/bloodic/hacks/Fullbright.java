package net.bloodic.hacks;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.mixinterface.ISimpleOption;
import net.bloodic.settings.NumberSetting;
import net.minecraft.client.option.SimpleOption;

public class Fullbright extends Hack implements UpdateListener
{
	private double previousGamma;
	private boolean savedGamma;
	private final NumberSetting brightness;
	
	public Fullbright()
	{
		super("Fullbright", "hacks.descs.fullbright", Category.RENDER);
		brightness = addSetting(new NumberSetting("Brightness", "Gamma override value.", 16.0, 1.0, 20.0, 0.5));
	}
	
	@Override
	protected void onEnable()
	{
		SimpleOption<Double> gamma = MC.options.getGamma();
		previousGamma = gamma.getValue();
		savedGamma = true;
		
		events().add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		SimpleOption<Double> gamma = MC.options.getGamma();
		double target = brightness.getValue();
		if (gamma.getValue() < target) {
			ISimpleOption<Double> opt = ISimpleOption.get(MC.options.getGamma());
			opt.forceSetValue(target);
		}
	}
	
	@Override
	protected void onDisable()
	{
		events().remove(UpdateListener.class, this);
		
		if (savedGamma) {
			MC.options.getGamma().setValue(previousGamma);
		}
	}
}

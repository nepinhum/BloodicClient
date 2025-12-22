package net.bloodic.settings;

public class BooleanSetting extends Setting<Boolean>
{
	public BooleanSetting(String name, String description, boolean defaultValue)
	{
		super(name, description, defaultValue);
	}

	public void toggle()
	{
		value = !value;
	}

	@Override
	public void next()
	{
		toggle();
	}

	@Override
	public void previous()
	{
		toggle();
	}

	@Override
	public String getDisplayValue()
	{
		return value ? "On" : "Off";
	}
}

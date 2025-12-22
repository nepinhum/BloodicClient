package net.bloodic.settings;

public class EnumSetting<E extends Enum<E>> extends Setting<E>
{
	private final E[] values;

	public EnumSetting(String name, String description, E defaultValue, Class<E> enumClass)
	{
		super(name, description, defaultValue);
		this.values = enumClass.getEnumConstants();
	}

	@Override
	public void next()
	{
		int idx = value.ordinal();
		int next = (idx + 1) % values.length;
		value = values[next];
	}

	@Override
	public void previous()
	{
		int idx = value.ordinal();
		int prev = (idx - 1 + values.length) % values.length;
		value = values[prev];
	}

	@Override
	public String getDisplayValue()
	{
		return value.name();
	}
}

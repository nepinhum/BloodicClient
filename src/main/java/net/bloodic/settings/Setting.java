package net.bloodic.settings;

public abstract class Setting<T>
{
	private final String name;
	private final String description;
	protected final T defaultValue;

	protected T value;

	protected Setting(String name, String description, T defaultValue)
	{
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public void reset()
	{
		value = defaultValue;
	}

	public abstract void next();

	public abstract void previous();

	public abstract String getDisplayValue();
}

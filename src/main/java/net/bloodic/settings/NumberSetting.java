package net.bloodic.settings;

public class NumberSetting extends Setting<Double>
{
	private final double min;
	private final double max;
	private final double step;

	public NumberSetting(String name, String description, double defaultValue,
		double min, double max, double step)
	{
		super(name, description, clamp(defaultValue, min, max));
		this.min = min;
		this.max = max;
		this.step = step <= 0 ? 0.1 : step;
	}

	@Override
	public void next()
	{
		setValue(value + step);
	}

	@Override
	public void previous()
	{
		setValue(value - step);
	}

	@Override
	public void setValue(Double value)
	{
		this.value = clamp(value, min, max);
	}

	private static double clamp(double value, double min, double max)
	{
		return Math.max(min, Math.min(max, value));
	}

	@Override
	public String getDisplayValue()
	{
		return String.format("%.2f", value);
	}
}

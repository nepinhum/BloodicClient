package net.bloodic.update;

import java.util.Arrays;

import net.fabricmc.loader.api.FabricLoader;

public final class Version implements Comparable<Version>
{
	private final String value;
	private final int[] numbers;
	private final String suffix;
	
	private Version(String value, int[] numbers, String suffix)
	{
		this.value = value;
		this.numbers = numbers;
		this.suffix = suffix;
	}
	
	public static Version parse(String raw)
	{
		if (raw == null || raw.isBlank())
			return new Version("0.0.0", new int[] {0, 0, 0}, "");
		
		String value = raw.trim();
		String numericPart = value;
		String suffix = "";
		
		int dashIndex = value.indexOf('-');
		if (dashIndex >= 0) {
			numericPart = value.substring(0, dashIndex);
			suffix = value.substring(dashIndex + 1);
		}
		
		String[] tokens = numericPart.split("\\.");
		int[] numbers = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			try {
				numbers[i] = Integer.parseInt(tokens[i]);
			} catch (NumberFormatException e) {
				numbers[i] = 0;
			}
		}
		
		return new Version(value, numbers, suffix);
	}
	
	public static Version current()
	{
		return FabricLoader.getInstance()
			.getModContainer("bloodic")
			.map(container -> parse(container.getMetadata()
				.getVersion()
				.getFriendlyString()))
			.orElse(parse("0.0.0"));
	}
	
	@Override
	public int compareTo(Version other)
	{
		int maxLength = Math.max(numbers.length, other.numbers.length);
		
		for (int i = 0; i < maxLength; i++) {
			int left = i < numbers.length ? numbers[i] : 0;
			int right = i < other.numbers.length ? other.numbers[i] : 0;
			
			if (left != right)
				return Integer.compare(left, right);
		}
		
		boolean thisMcSuffix = isMinecraftSuffix(suffix);
		boolean otherMcSuffix = isMinecraftSuffix(other.suffix);

		// Ignore Minecraft version suffixes when base versions match
		if ((thisMcSuffix && otherMcSuffix)
			|| (thisMcSuffix && other.suffix.isEmpty())
			|| (otherMcSuffix && suffix.isEmpty()))
			return 0;
		
		if (suffix.isEmpty() && !other.suffix.isEmpty())
			return 1;
		if (!suffix.isEmpty() && other.suffix.isEmpty())
			return -1;
		
		return suffix.compareTo(other.suffix);
	}
	
	private boolean isMinecraftSuffix(String value)
	{
		if (value == null || value.isEmpty())
			return false;
		
		String upper = value.toUpperCase();
		return upper.startsWith("MC");
	}
	
	public boolean isNewerThan(Version other)
	{
		return compareTo(other) > 0;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
	
	public String getSuffix()
	{
		return suffix;
	}
	
	public int[] getNumbers()
	{
		return Arrays.copyOf(numbers, numbers.length);
	}
}

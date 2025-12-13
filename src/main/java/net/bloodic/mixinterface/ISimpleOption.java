package net.bloodic.mixinterface;

import net.minecraft.client.option.SimpleOption;

public interface ISimpleOption<T>
{
	void forceSetValue(T value);
	
	@SuppressWarnings("unchecked")
	static <T> ISimpleOption<T> get(SimpleOption<T> option)
	{
		return (ISimpleOption<T>)(Object)option;
	}
}

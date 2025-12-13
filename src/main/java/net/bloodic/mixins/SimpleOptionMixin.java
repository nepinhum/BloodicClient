package net.bloodic.mixins;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.bloodic.mixinterface.ISimpleOption;
import net.minecraft.client.option.SimpleOption;

@Mixin(SimpleOption.class)
public abstract class SimpleOptionMixin<T> implements ISimpleOption<T>
{
	@Shadow
	private T value;
	
	@Shadow
	private Consumer<T> changeCallback;
	
	@Override
	public void forceSetValue(T newValue)
	{
		value = newValue;
		if (changeCallback != null)
			changeCallback.accept(newValue);
	}
}

package net.bloodic.event;

public abstract class Event<T extends Listener>
{
	public abstract void fire(T listener);

	public abstract Class<T> getListenerType();
}

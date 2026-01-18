package net.bloodic.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AntiKick
{
	private final int intervalTicks;
	private final double downVelocity;
	private int ticks;

	public AntiKick(int intervalTicks, double downVelocity)
	{
		this.intervalTicks = Math.max(1, intervalTicks);
		this.downVelocity = downVelocity;
	}

	public void reset()
	{
		ticks = 0;
	}

	public void tick(ClientPlayerEntity player, boolean active)
	{
		if (!active || player.isOnGround()) {
			reset();
			return;
		}

		ticks++;
		if (ticks < intervalTicks)
			return;

		ticks = 0;
		Vec3d velocity = player.getVelocity();
		player.setVelocity(velocity.x, downVelocity, velocity.z);
	}
}

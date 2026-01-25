package net.bloodic.hacks;

import net.bloodic.events.PacketOutputListener;
import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.minecraft.block.AirBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public class Jesus extends Hack implements UpdateListener, PacketOutputListener
{
    private int tickTimer = 10;
    private int packetTimer = 0;

    public Jesus()
    {
        super("Jesus", "hacks.descs.jesus", Category.MOVEMENT);

    }

    @Override
    protected void onEnable()
    {
        events().add(UpdateListener.class, this);
        events().add(PacketOutputListener.class, this);
    }

    @Override
    protected void onDisable()
    {
        events().remove(UpdateListener.class, this);
        events().remove(PacketOutputListener.class, this);
    }

    @Override
    public void onUpdate() {
        if (MC.options.sneakKey.isPressed())
            return;

        ClientPlayerEntity player = MC.player;
        if (player != null) {
            if (player.isTouchingWater() || player.isInLava()) {
                Vec3d velocity = player.getVelocity();
                player.setVelocity(velocity.x, 0.11, velocity.z);
                tickTimer = 0;
                return;
            }

            Vec3d velocity = player.getVelocity();
            if (tickTimer == 0) {
                player.setVelocity(velocity.x, 0.30, velocity.z);
            } else if (tickTimer == 1) {
                player.setVelocity(velocity.x, 0, velocity.z);
            }

            tickTimer++;
        }
    }

    @Override
    public void onSentPacket(PacketOutputEvent event)
    {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet))
            return;

        if (!(packet instanceof PlayerMoveC2SPacket.PositionAndOnGround
                || packet instanceof PlayerMoveC2SPacket.Full))
            return;

        ClientPlayerEntity player = MC.player;
        if (player == null)
            return;

        // in water
        if (player.isTouchingWater())
            return;

        // fall distance
        if (player.fallDistance > 3F)
            return;

        if (!isOverLiquid())
            return;

        // no input -> cancel
        if (player.input == null)
        {
            event.cancel();
            return;
        }

        // waiting for timer
        packetTimer++;
        if (packetTimer < 4)
            return;

        // cancel the previous packet
        event.cancel();

        // get coords (**l here)
        double x = packet.getX(player.getX());
        double y = packet.getY(player.getY());
        double z = packet.getZ(player.getZ());
        y += 0.05;

        Packet<?> newPacket;
        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround) {
            newPacket = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true);
        } else if (packet instanceof PlayerMoveC2SPacket.Full) {
            newPacket = new PlayerMoveC2SPacket.Full(
                    x, y, z,
                    packet.getYaw(player.getYaw()),
                    packet.getPitch(player.getPitch()),
                    true
            );
        } else {
            // fallback
            newPacket = packet;
        }

        MC.player.networkHandler.getConnection().send(newPacket);
    }

    private boolean isOverLiquid()
    {
        ClientPlayerEntity player = MC.player;
        if (player == null)
            return false;

        Box box = player.getBoundingBox().offset(0, -0.5, 0);

        AtomicBoolean foundLiquid = new AtomicBoolean(false);
        AtomicBoolean foundSolid = new AtomicBoolean(false);

        BlockPos.stream(box)
                .map(pos -> player.getWorld().getBlockState(pos).getBlock())
                .forEach(block -> {
                    if (block instanceof FluidBlock)
                        foundLiquid.set(true);
                    else if (!(block instanceof AirBlock))
                        foundSolid.set(true);
                });

        return foundLiquid.get() && !foundSolid.get();
    }

    public boolean shouldBeSolid()
    {
        ClientPlayerEntity player = MC.player;

        return isEnabled()
                && player != null
                && player.fallDistance <= 3F
                && !MC.options.sneakKey.isPressed()
                && !player.isTouchingWater()
                && !player.isInLava();
    }
}

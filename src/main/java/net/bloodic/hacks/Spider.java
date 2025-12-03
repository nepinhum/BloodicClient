package net.bloodic.hacks;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.network.ClientPlayerEntity;
import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.minecraft.util.math.Vec3d;

public class Spider extends Hack implements UpdateListener
{

    public Spider()
    {
        super("Spider", "Makes you Peter Parker.", Hack.Category.MOVEMENT, GLFW.GLFW_KEY_J);
    }

    @Override
    protected void onEnable()
    {
        events().add(UpdateListener.class, this);
    }

    @Override
    public void onUpdate()
    {
        ClientPlayerEntity player = MC.player;
        if (player != null) {
            if (!player.horizontalCollision)
                return;

            Vec3d velocity = player.getVelocity();
            if (velocity.y >= 0.2)
                return;

            player.setVelocity(velocity.x, 0.2, velocity.z);
        }
    }

    @Override
    protected void onDisable()
    {
        events().remove(UpdateListener.class, this);
    }
}

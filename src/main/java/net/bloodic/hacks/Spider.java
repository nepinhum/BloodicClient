package net.bloodic.hacks;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.network.ClientPlayerEntity;
import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class Spider extends Hack implements UpdateListener
{
    private final NumberSetting climbVelocity;

    public Spider()
    {
        super("Spider", "Makes you Peter Parker.", Hack.Category.MOVEMENT, GLFW.GLFW_KEY_J);
        climbVelocity = addSetting(new NumberSetting("Climb Velocity", "Upward push when on a wall.", 0.20, 0.10, 0.60, 0.05));
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
            if (velocity.y >= climbVelocity.getValue())
                return;

            double climb = climbVelocity.getValue();
            player.setVelocity(velocity.x, climb, velocity.z);
        }
    }

    @Override
    protected void onDisable()
    {
        events().remove(UpdateListener.class, this);
    }
}

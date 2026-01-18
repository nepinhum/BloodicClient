package net.bloodic.hacks;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.BooleanSetting;
import net.bloodic.utils.AntiKick;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerAbilities;
import org.lwjgl.glfw.GLFW;

public class CreativeFlight extends Hack implements UpdateListener
{
    private final BooleanSetting antiKick;
    private final BooleanSetting noFall;
    private final AntiKick antiKickHelper = new AntiKick(20, -0.04);

    public CreativeFlight()
    {
        super("CreativeFlight", "hacks.descs.creativeflight", Category.MOVEMENT, GLFW.GLFW_KEY_O);
        antiKick = addSetting(new BooleanSetting("AntiKick", "Small downward pulses while hovering.", false));
        noFall = addSetting(new BooleanSetting("NoFall", "Reset fall distance while flying.", true));
    }

    @Override
    protected void onEnable()
    {
        CL.getHackManager().getHackByName("Flight").setEnabled(false);
        events().add(UpdateListener.class, this);
    }

    @Override
    protected void onDisable()
    {
        events().remove(UpdateListener.class, this);
        antiKickHelper.reset();

        ClientPlayerEntity player = MC.player;
        PlayerAbilities abilities = player.getAbilities();
        boolean creative = player.getAbilities().creativeMode;
        abilities.flying = creative && !player.isOnGround();
        abilities.allowFlying = creative;
        restoreKeyPresses();

    }

    @Override
    public void onUpdate()
    {
        ClientPlayerEntity player = MC.player;
        if (player == null)
            return;

        PlayerAbilities abilities = player.getAbilities();
        abilities.allowFlying = true;

        if (noFall.getValue())
            player.fallDistance = 0;

        antiKickHelper.tick(player, antiKick.getValue() && abilities.flying && shouldAntiKick(player));
    }

    private boolean shouldAntiKick(ClientPlayerEntity player)
    {
        if (MC.options.jumpKey.isPressed() || MC.options.sneakKey.isPressed())
            return false;

        if (player.input == null)
            return true;

        return player.input.movementForward == 0 && player.input.movementSideways == 0;
    }

    private void restoreKeyPresses()
    {
        KeyBinding[] keys = {
                MC.options.jumpKey, MC.options.sneakKey
        };

        for(KeyBinding key : keys)
            key.setPressed(false);
    }
}

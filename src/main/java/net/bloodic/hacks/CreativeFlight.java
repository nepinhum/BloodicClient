package net.bloodic.hacks;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.BooleanSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerAbilities;
import org.lwjgl.glfw.GLFW;

// TODO Again a flight hack: Again not safe against servers :P
public class CreativeFlight extends Hack implements UpdateListener
{
    private final BooleanSetting antiKick;

    public CreativeFlight()
    {
        super("CreativeFlight", "hacks.descs.creativeflight", Category.MOVEMENT, GLFW.GLFW_KEY_O);
        antiKick = addSetting(new BooleanSetting("AntiKick", "iyk yk.", false));
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

        if (antiKick.getValue() && abilities.flying) {
            // TODO: antikick implantation
        }
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

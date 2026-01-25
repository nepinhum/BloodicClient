package net.bloodic.hacks;

import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;
import net.bloodic.settings.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;

public class Twerk extends Hack implements UpdateListener
{
    private final NumberSetting twerkSpeed;

    private int timer = 0;

    public Twerk()
    {
        super("Twerk", "hacks.descs.twerk", Category.FUN);
        twerkSpeed = addSetting(new NumberSetting("Twerk speed", "You know!", 5.0, 5.0, 10.0, 1));
    }

    @Override
    protected void onEnable()
    {
        events().add(UpdateListener.class, this);
    }

    @Override
    protected void onDisable()
    {
        events().remove(UpdateListener.class, this);
        MC.options.sneakKey.setPressed(false);
    }

    @Override
    public void onUpdate()
    {
        ClientPlayerEntity player = MC.player;
        if (player != null) {
            timer++;
            if (timer < 10 - twerkSpeed.getValue())
                return;

            KeyBinding sneakKey = MC.options.sneakKey;
            sneakKey.setPressed(!sneakKey.isPressed());
            timer = -1;
        }
    }
}

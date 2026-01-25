package net.bloodic.hacks;

import net.bloodic.config.DontSaveState;
import net.bloodic.events.UpdateListener;
import net.bloodic.hack.Hack;

public class Panic extends Hack implements UpdateListener, DontSaveState
{
    public Panic()
    {
        super("Panic", "hacks.descs.panic", Category.OTHER);
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
    }

    @Override
    public void onUpdate()
    {
        for (Hack hack : CL.getHackManager().getEnabledHacks()) {
            if (hack != this) {
                hack.setEnabled(false);
            }
        }

        setEnabled(false);
    }
}

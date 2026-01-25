package net.bloodic.keybinds;

import java.lang.reflect.Field;

import net.bloodic.hack.Hack;

public class Keybind
{
    private final int key;

    public Keybind(int key)
    {
        this.key = key;
    }

    public int getKey()
    {
        return key;
    }

    public static int findPossibleKeybind(Hack hack)
    {
        if (hack == null)
            return 0;

        String className = hack.getClass().getSimpleName();
        try {
            Field field = DefaultKeybinds.class.getField(className);
            if (field.getType() == int.class)
                return field.getInt(null);
        } catch (ReflectiveOperationException ignored) {
        }

        return 0; // TODO We probably shouldn't return 0, since it will be the key of a Hack..
    }
}

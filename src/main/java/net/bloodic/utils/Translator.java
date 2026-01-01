package net.bloodic.utils;

import net.minecraft.text.Text;

public class Translator
{
    private Translator()
    {}

    public static Text tr(String key, Object... args)
    {
        return Text.translatable(key, args);
    }
}

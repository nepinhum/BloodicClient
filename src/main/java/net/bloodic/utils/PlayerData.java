package net.bloodic.utils;

import net.bloodic.BloodicClient;
import net.bloodic.hack.HackManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;

public record PlayerData(boolean invulnerable, boolean immuneToFallDamage, boolean creativeFlight,
                         boolean flying, boolean jesus, boolean spider)
{
    private static final BloodicClient CL = BloodicClient.INSTANCE;
    private static final MinecraftClient MC = BloodicClient.MC;

    public static PlayerData get()
    {
        HackManager manager = CL.getHackManager();
        PlayerAbilities mcAbilities = MC.player.getAbilities();

        boolean invulnerable = mcAbilities.invulnerable || mcAbilities.creativeMode;
        boolean immuneToFallDamage = invulnerable; // TODO : NoFall
        boolean creativeFlight = mcAbilities.flying;
        boolean flying = creativeFlight || manager.getHackByName("Flight").isEnabled();
        boolean jesus = manager.getHackByName("Jesus").isEnabled();
        boolean spider = manager.getHackByName("Spider").isEnabled();

        return new PlayerData(invulnerable, immuneToFallDamage, creativeFlight, flying, jesus, spider);
    }
}

package net.bloodic.command;

import net.bloodic.BloodicClient;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public abstract class BaseCommand
{
    protected final BloodicClient CL = BloodicClient.INSTANCE;
    protected final MinecraftClient MC = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final String[] syntax;

    public BaseCommand(String name, String description, String... syntax)
    {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        Objects.requireNonNull(syntax);

        if(syntax.length > 0)
            syntax[0] = "Syntax: " + syntax[0];
        this.syntax = syntax;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String[] getSyntax()
    {
        return syntax;
    }

    public abstract void execute(String[] args) throws CmdException;
}

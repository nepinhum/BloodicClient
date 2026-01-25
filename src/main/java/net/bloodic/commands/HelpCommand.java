package net.bloodic.commands;

import java.util.stream.Collectors;

import net.bloodic.BloodicClient;
import net.bloodic.command.BaseCommand;
import net.bloodic.command.CmdException;
import net.bloodic.registry.impl.CommandRegistry;
import net.bloodic.utils.ChatUtils;
import net.minecraft.text.Text;

public class HelpCommand extends BaseCommand
{
	private final CommandRegistry registry;

	public HelpCommand(CommandRegistry registry)
	{
		super("bhelp", "Lists available commands.", "bhelp");
		this.registry = registry;
	}

	@Override
	public void execute(String[] args) throws CmdException
	{
		if (BloodicClient.MC == null || BloodicClient.MC.player == null)
			throw new CmdException("Player not available.");

		String list = registry.getAll().keySet().stream()
			.sorted()
			.collect(Collectors.joining(", "));

		ChatUtils.info( "Available commands: " + list);
	}
}

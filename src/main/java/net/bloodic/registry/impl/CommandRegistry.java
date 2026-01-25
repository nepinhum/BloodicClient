package net.bloodic.registry.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bloodic.command.BaseCommand;
import net.bloodic.commands.HelpCommand;
import net.bloodic.commands.IpCommand;
import net.bloodic.registry.BaseRegistry;

public class CommandRegistry extends BaseRegistry
{
	private final Map<String, BaseCommand> commands = new LinkedHashMap<>();

	@Override
	public void init()
	{
		register(new HelpCommand(this));
		register(new IpCommand());
	}

	public void register(BaseCommand command)
	{
		if (command == null)
			return;

		commands.put(command.getName().toLowerCase(), command);
	}

	public BaseCommand get(String name)
	{
		if (name == null)
			return null;

		return commands.get(name.toLowerCase());
	}

	public Map<String, BaseCommand> getAll()
	{
		return Collections.unmodifiableMap(commands);
	}
}

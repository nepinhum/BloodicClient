package net.bloodic.command;

import java.util.Arrays;

import net.bloodic.BloodicClient;
import net.bloodic.events.ChatOutputListener;
import net.bloodic.registry.impl.CommandRegistry;
import net.minecraft.text.Text;

public class CmdProcessor implements ChatOutputListener
{
	public static final String DOT_PREFIX = ".";
	public static final String SLASH_PREFIX = "/";
	private static final int MAX_SUGGESTIONS = 5;

	private final CommandRegistry registry;

	public CmdProcessor(CommandRegistry registry)
	{
		this.registry = registry;
	}

	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		String message = event.getMessage();
		if (message == null || message.length() <= 1)
			return;

		boolean dot = message.startsWith(DOT_PREFIX);
		boolean slash = message.startsWith(SLASH_PREFIX);
		if (!dot && !slash)
			return;

		String raw = message.substring(1).trim();
		if (raw.isEmpty())
			return;

		String[] parts = raw.split("\\s+");
		String name = parts[0];
		String[] args = Arrays.copyOfRange(parts, 1, parts.length);

		BaseCommand command = registry.get(name);
		if (command == null) {
			if (slash)
				return;

			sendMessage("Unknown command: " + name);
			sendMessage(getSuggestions(name));
			return;
		}

		event.cancel();

		try {
			command.execute(args);
		} catch (CmdException e) {
			String error = e.getMessage() != null ? e.getMessage() : "Command failed.";
			sendMessage(error);
			String[] syntax = command.getSyntax();
			if (syntax.length > 0)
				sendMessage(syntax[0]);
		} catch (Exception e) {
			sendMessage("Command error: " + e.getClass().getSimpleName());
		}
	}

	private void sendMessage(String message)
	{
		if (BloodicClient.MC == null || BloodicClient.MC.player == null)
			return;

		BloodicClient.MC.player.sendMessage(Text.literal(message), false);
	}

	private String getSuggestions(String name)
	{
		String prefix = name.toLowerCase();
		String list = registry.getAll().keySet().stream()
			.filter(cmd -> cmd.startsWith(prefix))
			.sorted()
			.limit(MAX_SUGGESTIONS)
			.reduce((a, b) -> a + ", " + b)
			.orElse("none");

		return "Suggestions: " + list;
	}
}

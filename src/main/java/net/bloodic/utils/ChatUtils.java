package net.bloodic.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ChatUtils
{
	private static final Text PREFIX = Text.literal("[Bloodic] ").formatted(Formatting.GRAY);

	private ChatUtils()
	{
	}

	public static void message(String message)
	{
		send(Text.literal(message).formatted(Formatting.WHITE), true);
	}

	public static void warning(String message)
	{
		send(Text.literal(message).formatted(Formatting.GOLD), true);
	}

	public static void error(String message)
	{
		send(Text.literal(message).formatted(Formatting.RED), true);
	}

	public static void info(String message)
	{
		send(Text.literal(message).formatted(Formatting.AQUA), true);
	}

	public static void raw(String message)
	{
		send(Text.literal(message), false);
	}

	private static void send(Text message, boolean prefixed)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc == null || mc.player == null)
			return;

		Text out = prefixed ? Text.empty().append(PREFIX).append(message) : message;
		mc.inGameHud.getChatHud().addMessage(out);
	}
}

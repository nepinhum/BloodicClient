package net.bloodic;

import org.lwjgl.glfw.GLFW;

import net.bloodic.event.EventManager;
import net.bloodic.hack.Hack;
import net.bloodic.hack.HackManager;
import net.bloodic.gui.ClickGuiScreen;
import net.bloodic.hud.InGameHud;
import net.bloodic.events.KeyPressListener;
import net.bloodic.events.UpdateListener;
import net.bloodic.update.BloodicUpdater;
import net.bloodic.update.Version;
import net.bloodic.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class BloodicClient implements ModInitializer
{
	private static boolean initialized;
	private static boolean DEBUG = false;
	
	public static BloodicClient INSTANCE;
	public static MinecraftClient MC;
	
	private EventManager eventManager;
	private HackManager hackManager;
	private InGameHud inGameHud;
	private Version version;
	private BloodicUpdater updater;
	private ConfigManager configManager;
	
	@Override
	public void onInitialize()
	{
		if (initialized)
			throw new RuntimeException(
				"BloodicClient.onInitialize() ran twice!");
		initialized = true;
		DEBUG = true; // for now
		
		System.out.println("Starting Bloodic Client...");
		
		INSTANCE = this;
		MC = MinecraftClient.getInstance();
		version = Version.current();
		eventManager = new EventManager();
		hackManager = new HackManager(this);
		configManager = new ConfigManager(hackManager);
		inGameHud = new InGameHud(eventManager);
		updater = new BloodicUpdater(version);
		configManager.load();
		updater.checkForUpdatesAsync();
		registerInternalListeners();
	}
	
	public HackManager getHackManager()
	{
		return hackManager;
	}
	
	public EventManager getEventManager()
	{
		return eventManager;
	}
	
	public Version getVersion()
	{
		return version;
	}
	
	public BloodicUpdater getUpdater()
	{
		return updater;
	}

	public ConfigManager getConfigManager()
	{
		return configManager;
	}
	
	public static boolean isDEBUG()
	{
		return DEBUG;
	}
	
	private void registerInternalListeners()
	{
			eventManager.add(KeyPressListener.class, event ->
			{
				if (event.getAction() != GLFW.GLFW_PRESS)
					return;
				
			if (event.getKeyCode() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
				if (MC.player == null)
					return;
				MC.setScreen(new ClickGuiScreen());
				return;
			}
				
				for (Hack hack : hackManager.getHacks()) {
					if (event.getKeyCode() == hack.getKey())
						hack.toggle();
				}
			});
		
		eventManager.add(UpdateListener.class, new UpdateListener()
		{
			private boolean announced;
			
			@Override
			public void onUpdate()
			{
				if (announced || updater == null)
					return;
				
				if (updater.getStatus() == BloodicUpdater.Status.FAILED) {
					announced = true;
					System.out.println("Bloodic update check failed: "
						+ updater.getLastError());
					return;
				}
				
				if (updater.getStatus() != BloodicUpdater.Status.READY)
					return;
				
				if (!updater.isUpdateAvailable()) {
					announced = true;
					return;
				}
				
				if (MC.player == null)
					return;
				
				announced = true;
				MC.player.sendMessage(Text.literal("Update available: "
					+ updater.getLatestVersion() + " (you have " + version + ")"),
					false);
				MC.player.sendMessage(Text.literal("Download: "
					+ updater.getDownloadPage()), false);
			}
		});
	}
}

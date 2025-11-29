package net.bloodic;

import org.lwjgl.glfw.GLFW;

import net.bloodic.event.EventManager;
import net.bloodic.hack.Hack;
import net.bloodic.hack.HackManager;
import net.bloodic.hud.InGameHud;
import net.bloodic.events.KeyPressListener;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

public class BloodicClient implements ModInitializer
{
	private static boolean initialized;
	private static boolean DEBUG = false;
	
	public static BloodicClient INSTANCE;
	public static MinecraftClient MC;
	
	private EventManager eventManager;
	private HackManager hackManager;
	private InGameHud inGameHud;
	
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
		eventManager = new EventManager();
		hackManager = new HackManager(this);
		inGameHud = new InGameHud(eventManager);
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
			
			for (Hack hack : hackManager.getHacks()) {
				if (event.getKeyCode() == hack.getKey())
					hack.toggle();
			}
			
			/*if(event.getKeyCode() == GLFW.GLFW_KEY_RIGHT_SHIFT)
				MC.setScreen(inGameHud.getClickGui());*/
		});
	}
}

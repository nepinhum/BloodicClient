package net.bloodic;

import org.lwjgl.glfw.GLFW;

import net.bloodic.hack.Hack;
import net.bloodic.hack.HackManager;
import net.bloodic.hud.InGameHud;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class BloodicClient implements ModInitializer{
	
	private static boolean initialized;
	private static boolean DEBUG = false;
	
	public static BloodicClient INSTANCE;
	public static MinecraftClient MC;
	private HackManager hackManager;
	private InGameHud inGameHud;
	
	@Override
	public void onInitialize(){
		if(initialized)
			throw new RuntimeException(
				"BloodicClient.onInitialize() ran twice!");
		initialized = true;
		DEBUG = true; // for now
		
		System.out.println("Starting Bloodic Client...");
		
		INSTANCE = this;
		MC = MinecraftClient.getInstance();
		hackManager = new HackManager(this);
		inGameHud = new InGameHud();
	}
	
	public HackManager getHackManager(){
		return hackManager;
	}
	
	public static boolean isDEBUG(){
		return DEBUG;
	}
	
	public void onRenderGUI(MatrixStack matrices, float partialTicks){
		inGameHud.renderGUI(matrices, partialTicks);
	}
	
	public void onKeyPress(int key, int action){
		if(action == GLFW.GLFW_PRESS){
			for(Hack hack : hackManager.getHacks()){
				if(key == hack.getKey()) hack.toggle();
			}
		}
		
		/*if(key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			MC.setScreen(inGameHud.getClickGui());
		}*/
	}
	
	public void onUpdate(){
		if(MC.player != null){
			for(Hack hack : hackManager.getEnabledHacks()){
				hack.onUpdate();
			}
		}
	}
}

package net.bloodic.hack;

import net.bloodic.BloodicClient;
import net.minecraft.client.MinecraftClient;

public class Hack{
	
	protected static final BloodicClient CL = BloodicClient.INSTANCE;
	protected static final MinecraftClient MC = BloodicClient.MC;
	
	private final String name;
	private final String description;
	private final Category category;
	private final int key;
	
	private boolean enabled;
	
	public enum Category{
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		WORLD("World"),
		RENDER("Render"),
		EXPLOIT("Exploit");
		
		public String name;
		
		private Category(String name) {
			this.name = name;
		}
	}
	
	public Hack(String name, String description, Category category, int key){
		this.name = name;
		this.description = description;
		this.category = category;
		this.key = key;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public Category getCategory(){
		return category;
	}
	
	public int getKey(){
		return key;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public final void toggle(){
		this.enabled = !this.enabled;
		
		if(enabled) {
			onEnable();
		} else {
			onDisable();
		}
	}
	
	public void onUpdate(){
		
	}
	
	protected void onEnable(){
	}
	
	protected void onDisable(){
	}
}

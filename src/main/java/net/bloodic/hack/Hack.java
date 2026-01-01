package net.bloodic.hack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.bloodic.BloodicClient;
import net.minecraft.client.MinecraftClient;
import net.bloodic.event.EventManager;
import net.bloodic.settings.Setting;
import net.minecraft.text.Text;

public class Hack
{
	protected static final BloodicClient CL = BloodicClient.INSTANCE;
	protected static final MinecraftClient MC = BloodicClient.MC;
	
	private final String name;
	private final String description;
	private final Category category;
	private final int key;
	
	private boolean enabled;
	private final ArrayList<Setting<?>> settings = new ArrayList<>();
	
	public enum Category
	{
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		WORLD("World"),
		RENDER("Render"),
		EXPLOIT("Exploit"),
		FUN("Fun"),
		OTHER("Other");
		
		public String name;
		
		private Category(String name)
		{
			this.name = name;
		}
	}
	
	public Hack(String name, String description, Category category, int key)
	{
		this.name = name;
		this.description = description;
		this.category = category;
		this.key = key;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public Category getCategory()
	{
		return category;
	}
	
	public int getKey()
	{
		return key;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}

	public final void setEnabled(boolean enabled)
	{
		if (this.enabled == enabled)
			return;

		this.enabled = enabled;

		if (enabled) {
			onEnable();
		} else {
			onDisable();
		}

		if (CL != null && CL.getConfigManager() != null
			&& !CL.getConfigManager().isSaveSuppressed()) {
			CL.getConfigManager().saveAsync();
		}
	}

	public List<Setting<?>> getSettings()
	{
		return Collections.unmodifiableList(settings);
	}
	
	public final void toggle()
	{
		setEnabled(!this.enabled);
	}
	
	protected void onEnable()
	{
	}
	
	protected void onDisable()
	{
	}
	
	protected final EventManager events()
	{
		return BloodicClient.INSTANCE.getEventManager();
	}

	protected final <S extends Setting<?>> S addSetting(S setting)
	{
		settings.add(setting);
		return setting;
	}

	public void resetSettings()
	{
		for (Setting<?> setting : settings) {
			setting.reset();
		}
	}
}

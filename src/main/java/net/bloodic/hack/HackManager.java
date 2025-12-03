package net.bloodic.hack;

import java.util.ArrayList;
import java.util.List;

import net.bloodic.BloodicClient;
import net.bloodic.hack.Hack.Category;
import net.bloodic.hacks.AutoSprint;
import net.bloodic.hacks.Flight;
import net.bloodic.hacks.Fullbright;
import net.bloodic.hacks.Spider;

public class HackManager
{
	private List<Hack> hacks = new ArrayList<>();
	
	public HackManager(BloodicClient client)
	{
		addHacks();
	}
	
	public List<Hack> getHacks()
	{
		return hacks;
	}
	
	public List<Hack> getEnabledHacks()
	{
		List<Hack> enabled = new ArrayList<>();
		for (Hack hack : hacks) {
			if(hack.isEnabled()) enabled.add(hack);
		}
		
		return enabled;
	}
	
	public List<Hack> getHacksInCategory(Category category)
	{
		List<Hack> categoryHacks = new ArrayList<>();
		
		for (Hack hack : hacks) {
			if (hack.getCategory() == category) {
				categoryHacks.add(hack);
			}
		}
		
		return categoryHacks;
	}
	
	private void addHacks()
	{
		hacks.add(new Flight());
		hacks.add(new AutoSprint());
		hacks.add(new Fullbright());
		hacks.add(new Spider());
	}
}

package net.bloodic.hud;

import net.minecraft.client.util.math.MatrixStack;

public class InGameHud{
	
	private HackListHud listHud;
	//private ClickGui clickGui;
	
	public InGameHud(){
		listHud = new HackListHud();
		//clickGui = new ClickGui();
	}
	
	public void renderGUI(MatrixStack matrices, float partialTicks){
		if(listHud != null){
			listHud.render(matrices, partialTicks);
		}
	}
}

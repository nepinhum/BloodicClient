package net.bloodic.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class BloodicGameTests implements FabricGameTest
{
	@GameTest(templateName = EMPTY_STRUCTURE)
	public void sanity(TestContext context)
	{
		context.complete();
	}
}

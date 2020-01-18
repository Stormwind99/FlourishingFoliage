package com.wumple.flourishingfoliage.repair;

import com.wumple.util.blockrepair.RepairDebug;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//@EventBusSubscriber(modid = Reference.MOD_ID, bus = Bus.FORGE)
@EventBusSubscriber
public class LeavesRepairDebug extends RepairDebug
{
	/// Draw debug screen extras
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onDrawOverlay2(final RenderGameOverlayEvent.Text e)
	{
		RepairDebug.onDrawOverlay(e);
	}
}
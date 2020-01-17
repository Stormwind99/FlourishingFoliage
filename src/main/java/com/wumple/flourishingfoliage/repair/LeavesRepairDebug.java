package com.wumple.flourishingfoliage.repair;

import com.wumple.util.blockrepair.RepairDebug;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LeavesRepairDebug extends RepairDebug
{
	/// Draw debug screen extras
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onDrawOverlay2(final RenderGameOverlayEvent.Text e)
	{
		RepairDebug.onDrawOverlay(e);
	}
}
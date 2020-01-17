package com.wumple.flourishingfoliage.repair;

import java.util.concurrent.ThreadLocalRandom;

import com.wumple.flourishingfoliage.ConfigManager;
import com.wumple.util.blockrepair.RepairManager;
import com.wumple.util.misc.LeafUtil2;
import com.wumple.util.misc.TimeUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LeavesRepairManager extends RepairManager
{
    @Override
    protected Block getRepairingBlock()
    {
        return LeavesRepairObjectHolder.blockRepairing;
    }

    public static int getRandomTicksToRepair()
    {
        int min = ConfigManager.General.leafRegrowthRate.get() / 2;
        int max = ConfigManager.General.leafRegrowthRate.get() * 3 / 2;
        int seconds = ThreadLocalRandom.current().nextInt(min, max);
        int ticks = TimeUtil.TICKS_PER_SECOND * seconds;
        ticks = (int)((double)ticks * ConfigManager.Debugging.regrowModifier.get());
        return ticks;
    }

    @Override
    public int getTicksToRepair()
    {
        return getRandomTicksToRepair();
    }
    
    public boolean isBlockToRepair(Block block, BlockState blockstate, IWorld world, BlockPos pos)
    {
        return LeafUtil2.isLeaves(world, pos);
    }
    
    public boolean isTemporaryBreak(PlayerEntity player)
    {
        return player.getHeldItemMainhand().isEmpty()
                || (player.getHeldItemMainhand().getItem() != Items.SHEARS);
    }

    @Override
	public boolean shouldRepair(PlayerEntity player, Block block, BlockState blockstate, IWorld world, BlockPos pos)
    {
        return isBlockToRepair(block, blockstate, world, pos) && isTemporaryBreak(player);
    }

    @Override
    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event)
    {
        super.onBreak(event);
    }
    
    /*
     * TODO Consider handling explosionEvent and repairing any leaves (assuming tree survives)
     * See CoroUtil.forge.EventHandlerForge#explosionEvent for example
     */
}

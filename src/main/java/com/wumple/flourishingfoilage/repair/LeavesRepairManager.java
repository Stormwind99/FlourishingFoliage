package com.wumple.flourishingfoilage.repair;

import java.util.concurrent.ThreadLocalRandom;

import com.wumple.flourishingfoilage.ModConfig;
import com.wumple.util.blockrepair.BlockRepairingBlock;
import com.wumple.util.blockrepair.RepairManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LeavesRepairManager extends RepairManager
{
    public static final int TICKS_PER_SECOND = 20;

    @Override
    protected BlockRepairingBlock BlockRepairingBlockFactory()
    {
        return new BlockLeavesRepairing();
    }

    @Override
    protected Class<? extends TileEntity> TileEntityRepairingBlockClass()
    {
        return TileEntityLeavesRepairing.class;
    }

    public static int getRandomTicksToRepair()
    {
        int min = ModConfig.regrowSettings.leafRegrowthRate / 2;
        int max = ModConfig.regrowSettings.leafRegrowthRate * 3 / 2;
        int seconds = ThreadLocalRandom.current().nextInt(min, max);
        int ticks = TICKS_PER_SECOND * seconds;
        if (ModConfig.zdebugging.debug)
        {
            ticks = (int)((double)ticks * ModConfig.zdebugging.regrowModifier);
        }
        return ticks;
    }
    
    public boolean isLeaves(Block block, IBlockState blockstate, World world, BlockPos pos)
    {
        return (block.isLeaves(blockstate, world, pos) || (block instanceof BlockLeaves));
    }
    
    public boolean isTemporaryBreak(EntityPlayer player)
    {
        return player.getHeldItemMainhand().isEmpty()
                || (player.getHeldItemMainhand().getItem() != Items.SHEARS);
    }

    // idea from LilRichy's RegrowableLeaves EventHandler.breakEvent()
    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event)
    {
        BlockPos pos = event.getPos();
        IBlockState blockstate = event.getState();
        World world = event.getWorld();
        Block block = blockstate.getBlock();
        EntityPlayer player = event.getPlayer();
        
        if (isLeaves(block, blockstate, world, pos))
        {
            if (isTemporaryBreak(player))
            {
                // replace with repairing block
                this.replaceBlock(world, blockstate, pos, getRandomTicksToRepair());
                
                // harvest original block
                block.harvestBlock(world, player, pos, blockstate, null, ItemStack.EMPTY);
                
                // don't handle event later since we did handled it
                event.setCanceled(true);
            }
        }
    }
    
    /*
     * TODO Consider handling explosionEvent and repairing any leaves (assuming tree survives)
     * See CoroUtil.forge.EventHandlerForge#explosionEvent for example
     */
}

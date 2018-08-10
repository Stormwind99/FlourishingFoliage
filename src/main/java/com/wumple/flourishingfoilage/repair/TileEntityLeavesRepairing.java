package com.wumple.flourishingfoilage.repair;

import com.wumple.flourishingfoilage.ModConfig;
import com.wumple.util.blockrepair.TileEntityRepairingBlock;
import com.wumple.util.misc.LeafUtil;
import com.wumple.util.misc.TimeUtil;

import net.minecraft.util.math.BlockPos;

public class TileEntityLeavesRepairing extends TileEntityRepairingBlock
{
    protected boolean isEnoughLight(BlockPos pos)
    {
        int lightLevel = getWorld().getLightFromNeighbors(getPos().up());
        return (lightLevel >= ModConfig.regrowSettings.lightRequiredToGrow);
    }
    
    @Override
    public long getExpirationTimeLength()
    {
        long ticks = ModConfig.regrowSettings.timeToGiveUp * TimeUtil.TICKS_PER_SECOND;
        if (ModConfig.zdebugging.debug)
        {
            ticks = (long)((double)ticks * ModConfig.zdebugging.regrowModifier);
        }
        return ticks;
    }
    
    @Override
    protected boolean canRepairBlock()
    {
        return(
                super.canRepairBlock()
                && getWorld().isAreaLoaded(getPos(), 1)
                && isEnoughLight(getPos())
                && ((!ModConfig.regrowSettings.growOutward) || LeafUtil.canLeavesGrowAtLocation(getWorld(), getPos())) 
                );
    }
    
    @Override
    protected void onCantRepairBlock()
    {
        super.onCantRepairBlock();
        
        // if expired, super would mark us invalid
        if (!isInvalid())
        {
            // try again later
            int ticks = LeavesRepairManager.getRandomTicksToRepair();
            setTicksToRepair(getWorld(), ticks);
            markDirty();
            LeavesRepairManager.log("rescheduling state: " + orig_blockState + " to " + getTimeToRepairAt() + " at " + this.getPos());
        }
    }
}

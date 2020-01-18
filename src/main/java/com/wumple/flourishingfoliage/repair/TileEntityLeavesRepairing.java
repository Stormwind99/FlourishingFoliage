package com.wumple.flourishingfoliage.repair;

import com.wumple.flourishingfoliage.ConfigManager;
import com.wumple.util.blockrepair.TileEntityRepairingBlock;
import com.wumple.util.misc.LeafUtil2;
import com.wumple.util.misc.TimeUtil;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class TileEntityLeavesRepairing extends TileEntityRepairingBlock
{
	public TileEntityLeavesRepairing()
	{
		super(LeavesRepairObjectHolder.RepairingBlock_Tile);
	}

	public TileEntityLeavesRepairing(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}
	
    protected boolean isEnoughLight(BlockPos pos)
    {
        int lightLevel = getWorld().getLight(getPos().up());
        return (lightLevel >= ConfigManager.General.lightRequiredToGrow.get());
    }
    
    @Override
    public long getExpirationTimeLength()
    {
        long ticks = ConfigManager.General.timeToGiveUp.get() * TimeUtil.TICKS_PER_SECOND;
        ticks = (long)((double)ticks * ConfigManager.Debugging.regrowModifier.get());
        return ticks;
    }
    
    @Override
    protected boolean canRepairBlock()
    {
        return(
                super.canRepairBlock()
                && getWorld().isAreaLoaded(getPos(), 1)
                && isEnoughLight(getPos())
                && ((!ConfigManager.General.growOutward.get()) || LeafUtil2.canLeavesGrowAtLocation(getWorld(), getPos())) 
                );
    }
    
    @Override
    protected void onCantRepairBlock()
    {
        super.onCantRepairBlock();

        this.validate();
        // if expired, super would mark us invalid
        if (!isRemoved())
        {
            // try again later
            int ticks = LeavesRepairManager.getRandomTicksToRepair();
            setTicksToRepair(getWorld(), ticks);
            markDirty();
            LeavesRepairManager.log("rescheduling state: " + repairState.orig_blockState + " to " + getTimeToRepairAt() + " at " + this.getPos());
        }
    }
}

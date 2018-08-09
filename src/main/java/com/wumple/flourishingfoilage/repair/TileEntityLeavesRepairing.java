package com.wumple.flourishingfoilage.repair;

import com.wumple.flourishingfoilage.ModConfig;
import com.wumple.util.blockrepair.TileEntityRepairingBlock;
import com.wumple.util.misc.LeafUtil;

import net.minecraft.util.math.BlockPos;

public class TileEntityLeavesRepairing extends TileEntityRepairingBlock
{
    protected boolean isEnoughLight(BlockPos pos)
    {
        int lightLevel = getWorld().getLightFromNeighbors(getPos().up());
        return (lightLevel >= ModConfig.regrowSettings.lightRequiredToGrow);
    }
    
    @Override
    protected boolean canRepairBlock()
    {
        return(
                super.canRepairBlock()
                && getWorld().isAreaLoaded(getPos(), 1)
                && isEnoughLight(getPos())
                && LeafUtil.canLeavesGrowAtLocation(getWorld(), getPos()) 
                );
    }
    
    @Override
    protected void onCantRepairBlock()
    {
        super.onCantRepairBlock();
        
        // try again later
        setTicksToRepair(getWorld(), LeavesRepairManager.getRandomTicksToRepair());
        
        // TODO - stop trying eventually and delete ourselves
        
        markDirty();
    }
}

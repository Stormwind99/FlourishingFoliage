package com.wumple.flourishingfoilage.repair;

import com.wumple.flourishingfoilage.ModConfig;
import com.wumple.util.blockrepair.BlockRepairingBlock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

public class BlockLeavesRepairing extends BlockRepairingBlock
{
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityLeavesRepairing();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        // show debug model if debugging, invisible if not debugging
        if (ModConfig.zdebugging.debug)
        {
            return EnumBlockRenderType.MODEL;
        }
        else
        {
            return EnumBlockRenderType.INVISIBLE;
            // MAYBE return super.getRenderType(state);
        }
    }
}

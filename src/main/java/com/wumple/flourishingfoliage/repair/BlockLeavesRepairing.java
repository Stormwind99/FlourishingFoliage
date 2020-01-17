package com.wumple.flourishingfoliage.repair;

import com.wumple.util.blockrepair.BlockRepairingBlock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockLeavesRepairing extends BlockRepairingBlock
{
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new TileEntityLeavesRepairing();
	}
}

package com.wumple.util.blockrepair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.Nullable;

import com.wumple.util.misc.LeafUtil2;

/*
 * Based on CoroUtil's TileEntityRepairingBlock
 * from https://github.com/Corosauce/CoroUtil
 */

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;

/*
 * Originally based on CoroUtil's BlockRepairingBlock
 * from https://github.com/Corosauce/CoroUtil
 */
public class TileEntityRepairingBlock extends TileEntity implements ITickableTileEntity, IRepairing
{
	// block state to restore later
	protected BlockState orig_blockState;

	// cached values of original block to use for this tile entity's block
	protected float orig_hardness = 1;
	protected float orig_explosionResistance = 1;

	// when to restore state
	protected long timeToRepairAt = 0;
	protected long creationTime = 0;

	public TileEntityRepairingBlock()
	{
		super(MyObjectHolder.RepairingBlock_Tile);
	}

	public TileEntityRepairingBlock(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	public void setTicksToRepair(IWorld world, int ticksToRepair)
	{
		long currentTime = world.getWorld().getGameTime();
		if (creationTime == 0)
		{
			creationTime = currentTime;
		}
		timeToRepairAt = currentTime + ticksToRepair;
		markDirty();
	}

	// override to change behavior when determining if repairing block has expired
	@Override
	public long getExpirationTimeLength()
	{
		// override to change behavior
		return 0;
	}

	@Override
	public long getTimeToRepairAt()
	{
		return timeToRepairAt;
	}

	@Override
	public long getTimeToGiveUpAt()
	{
		long timeExpiration = 0;
		long exp = getExpirationTimeLength();
		if ((creationTime != 0) && (exp > 0))
		{
			timeExpiration = creationTime + exp;
		}
		return timeExpiration;
	}

	protected boolean isTimeToRepair()
	{
		long currentTime = getWorld().getGameTime();
		return (currentTime >= timeToRepairAt);
	}

	public boolean isTimeToGiveUp()
	{
		long timeExpiration = getTimeToGiveUpAt();
		if (timeExpiration > 0)
		{
			long currentTime = getWorld().getGameTime();
			return (currentTime >= timeExpiration);
		}

		return false;
	}

	protected boolean isDataInvalid()
	{
		return orig_blockState == null || orig_blockState == this.getBlockState();
	}

	// override to change behavior when determining if block is repairable now
	protected boolean canRepairBlock()
	{
		// don't repair now if any Entity's are in our bounds
		//AxisAlignedBB aabb = this.getBlockState().getCollisionShape(this.getWorld(), this.getPos()).getBoundingBox();
		AxisAlignedBB aabb = orig_blockState.getCollisionShape(this.getWorld(), this.getPos()).getBoundingBox();
		aabb = aabb.offset(this.getPos());
		List<LivingEntity> listTest = this.getWorld().getEntitiesWithinAABB(LivingEntity.class, aabb);
		return (listTest.size() == 0);
	}

	// override to change behavior when a block can't be repaired now (aka
	// canRepairBlock() returned false)
	protected void onCantRepairBlock()
	{
		// override to change behavior
		if (isTimeToGiveUp())
		{
			giveUp();
		}
	}

	// override to change behavior before a block is restored
	protected void preRestoreBlock()
	{
		// do nothing by default
		// override to change behavior
	}

	// override to change behavior of restoring a block
	protected void coreRestoreBlock()
	{
		getWorld().setBlockState(this.getPos(), orig_blockState);
		markDirty();
	}

	protected void cancelAdjacentLeafDecay()
	{
		// try to untrigger leaf decay for those large trees too far from wood source
		// also undo it for neighbors around it
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					BlockPos posFix = pos.add(x, y, z);
					if (LeafUtil2.isLeaves(world, posFix))
					{
						try
						{
							RepairManager.log("restoring leaf to non decay state at pos: " + posFix);
							BlockState state = world.getBlockState(posFix);
							// modify just the DISTANCE property, leaving the rest as-is
							world.setBlockState(posFix, state.with(LeavesBlock.DISTANCE, Integer.valueOf(1)), 4);
						}
						catch (Exception ex)
						{
							// must be a modded block that doesn't use decay
							if (RepairManager.isDebugEnabled())
							{
								// debug log stack trace
								StringWriter sw = new StringWriter();
								PrintWriter pw = new PrintWriter(sw);
								ex.printStackTrace(pw);
								String sStackTrace = sw.toString(); // stack trace as a string

								RepairManager.log("Assume modded block not using decay: " + sStackTrace);
							}
						}
					}
				}
			}
		}

	}

	// override to change behavior after a block is restored
	protected void postRestoreBlock()
	{
		markDirty();

		cancelAdjacentLeafDecay();
	}

	// override to change behavior to restore a block (after canRestoreBlock()
	// returns true)
	protected void restoreBlock()
	{
		RepairManager.log("restoring block to state: " + orig_blockState + " at " + this.getPos());
		preRestoreBlock();
		coreRestoreBlock();
		postRestoreBlock();
	}

	protected void giveUp()
	{
		RepairManager.log("giving up on state: " + orig_blockState + " at " + this.getPos());
		getWorld().setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
		markDirty();
		this.markDirty();
	}

	// implements ITickableTileEntity
	@Override
	public void tick()
	{
		if (!getWorld().isRemote)
		{
			if (isTimeToRepair())
			{
				// if for some reason data is invalid, remove block
				if (isDataInvalid())
				{
					RepairManager.log("invalid state for repairing block, removing, orig_blockState: " + orig_blockState
							+ " vs " + this.getBlockState());
					giveUp();
				}
				else
				{
					if (canRepairBlock())
					{
						restoreBlock();
					}
					else
					{
						onCantRepairBlock();
					}
				}
			}
		}
	}

	/*
	 * Read and write additional NBT data
	 */

	@Override
	public CompoundNBT write(CompoundNBT var1)
	{
		if (orig_blockState != null)
		{
			ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(this.orig_blockState.getBlock());
			var1.putString("orig_blockName", loc.toString());

			CompoundNBT stateNBT = NBTUtil.writeBlockState(this.orig_blockState);
			var1.put("orig_blockState2", stateNBT);
		}
		var1.putLong("timeToRepairAt", timeToRepairAt);
		var1.putLong("creationTime", creationTime);

		var1.putFloat("orig_hardness", orig_hardness);
		var1.putFloat("orig_explosionResistance", orig_explosionResistance);

		return super.write(var1);
	}

	@Override
	public void read(CompoundNBT var1)
	{
		super.read(var1);
		timeToRepairAt = var1.getLong("timeToRepairAt");
		creationTime = var1.getLong("creationTime");
		try
		{
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(var1.getString("orig_blockName")));
			if (block != null)
			{
				this.orig_blockState = NBTUtil.readBlockState(var1.getCompound("orig_blockState2"));
			}
		}
		catch (Exception ex)
		{
			if (RepairManager.isDebugEnabled())
			{
				ex.printStackTrace();
			}

			this.orig_blockState = Blocks.AIR.getDefaultState();
		}

		orig_hardness = var1.getFloat("orig_hardness");
		orig_explosionResistance = var1.getFloat("orig_explosionResistance");
	}

	/*
	 * Original block (to repair) data accessors
	 */

	public void init(IWorld world, int ticksToRepair, BlockState state, float hardness, float explosionResistance)
	{
		this.orig_blockState = state;
		this.orig_hardness = hardness;
		this.orig_explosionResistance = explosionResistance;
		setTicksToRepair(world, ticksToRepair);
		markDirty();
	}

	public BlockState getOrig_blockState()
	{
		return orig_blockState;
	}

	public float getOrig_hardness()
	{
		return orig_hardness;
	}

	public float getOrig_explosionResistance()
	{
		return orig_explosionResistance;
	}

	// ----------------------------------------------------------------------
	// Update custom data to client
	// from
	// https://github.com/Chisel-Team/Chisel/blob/1.10/dev/src/main/java/team/chisel/common/block/TileAutoChisel.java

	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(getPos(), 0, getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		CompoundNBT ret = super.getUpdateTag();
		ret.putLong("timeToRepairAt", timeToRepairAt);
		ret.putLong("creationTime", creationTime);
		return ret;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		handleUpdateTag(pkt.getNbtCompound());
		super.onDataPacket(net, pkt);
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag)
	{
		timeToRepairAt = tag.getLong("timeToRepairAt");
		creationTime = tag.getLong("creationTime");
		super.handleUpdateTag(tag);
	}
}
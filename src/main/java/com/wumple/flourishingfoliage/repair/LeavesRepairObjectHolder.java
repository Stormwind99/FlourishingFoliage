package com.wumple.flourishingfoliage.repair;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("flourishingfoliage")
public class LeavesRepairObjectHolder
{
    // ----------------------------------------------------------------------
    // Blocks, Items, etc.

    //@ObjectHolder("flourishingfoliage:leaves_repairing")
    public static /*final*/ Block blockRepairing = null;

    //@ObjectHolder("flourishingfoliage:leaves_repairing")
    public static /*final*/ BlockItem itemBlockRepairing = null;
    
    @ObjectHolder("flourishingfoliage:leaves_repairing")
	public static TileEntityType<TileEntityLeavesRepairing> RepairingBlock_Tile;

    // ----------------------------------------------------------------------
    // Events

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            blockRepairing = new BlockLeavesRepairing();
            blockRepairing.setRegistryName("flourishingfoliage:leaves_repairing");
            registry.register(blockRepairing);

        }
        
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();
            
            Item.Properties properties = new Item.Properties();
            
            itemBlockRepairing = new BlockItem(blockRepairing, properties);
            itemBlockRepairing.setRegistryName("flourishingfoliage:leaves_repairing");
            
            registry.register(itemBlockRepairing);
        }

        
		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event)
		{
			event.getRegistry().register(TileEntityType.Builder.create(TileEntityLeavesRepairing::new, blockRepairing)
					.build(null).setRegistryName("flourishingfoliage:leaves_repairing"));
		}
    }
}
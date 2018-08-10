package com.wumple.flourishingfoilage.repair;

import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("flourishingfoilage")
public class LeavesRepairObjectHolder
{
    // ----------------------------------------------------------------------
    // Blocks, Items, etc.

    //@ObjectHolder("flourishingfoilage:leaves_repairing")
    public static /*final*/ Block blockRepairing = null;

    //@ObjectHolder("flourishingfoilage:leaves_repairing")
    public static /*final*/ ItemBlock itemBlockRepairing = null;

    // ----------------------------------------------------------------------
    // Events

    @EventBusSubscriber
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            blockRepairing = RegistrationHelpers.regHelper(registry, new BlockLeavesRepairing(), "flourishingfoilage:leaves_repairing");
        }
        
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();
            
            itemBlockRepairing = RegistrationHelpers.registerItemBlock(registry, blockRepairing);
            
            registerTileEntities();
        }
    
        public static void registerTileEntities()
        {
            RegistrationHelpers.registerTileEntity(TileEntityLeavesRepairing.class, "flourishingfoilage:leaves_repairing");
        }
    }
}
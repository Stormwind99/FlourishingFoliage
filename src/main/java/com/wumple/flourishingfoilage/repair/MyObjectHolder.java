package com.wumple.flourishingfoilage.repair;

import com.wumple.util.Reference;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("flourishingfoilage")
public class MyObjectHolder
{
    // ----------------------------------------------------------------------
    // Blocks, Items, etc.

    //@ObjectHolder("flourishingfoilage:leaves_repairing")
    public static /*final*/ Block blockRepairing = null;

    //@ObjectHolder("flourishingfoilage:leaves_repairing")
    public static /*final*/ ItemBlock itemBlockRepairing = null;

    // ----------------------------------------------------------------------
    // Events

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            blockRepairing = RegistrationHelpers.regHelper(registry, new BlockLeavesRepairing());
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
            RegistrationHelpers.registerTileEntity(TileEntityLeavesRepairing.class, "wumpleutil:leaves_repairing");
        }
    }
}
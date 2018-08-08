package com.wumple.flourishingfoilage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.flourishingfoilage.repair.LeavesRepairManager;
import com.wumple.util.mod.ModBase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.DEPENDENCIES,
        updateJSON = Reference.UPDATEJSON, certificateFingerprint = Reference.FINGERPRINT)
public class FlourishingFoilage extends ModBase
{
    @Mod.Instance(Reference.MOD_ID)
    public static FlourishingFoilage instance;
    
    protected LeavesRepairManager manager = null;
    
    public LeavesRepairManager getManager()
    {
        if (manager == null)
        {
            manager = new LeavesRepairManager();
        }
        return manager;
    }

    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @EventHandler
    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        MinecraftForge.EVENT_BUS.register(getManager());
    }

    @EventHandler
    @Override
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        super.onFingerprintViolation(event);
    }

    @Override
    public Logger getLoggerFromManager()
    {
        return LogManager.getLogger(Reference.MOD_ID);
    }
}

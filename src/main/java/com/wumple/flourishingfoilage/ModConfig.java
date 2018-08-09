package com.wumple.flourishingfoilage;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModConfig
{
    @Name(value = "Regrow Settings")
    @Config.Comment(value = "Settings related to regrow like rate, light level, grow outward")
    public static final RegrowSettings regrowSettings = new RegrowSettings();

    public static class RegrowSettings
    {
        @Name("Leaf regrow rate")
        @Config.Comment("Time leaves take to regrow on average, in seconds.")
        @RangeInt(min = 0)
        public int leafRegrowthRate = 180;

        @Name("Light required to grow")
        @Config.Comment("Light level required for leaves to start regrowing. 0 = no light required.")
        @RangeInt(min = 0, max = 15)
        public int lightRequiredToGrow = 9;

        @Name("Grow outward")
        @Config.Comment("Must leaves grow next to other leaves or logs (aka \"can sustain leaves\") to regrow")
        public boolean growOutward = true;
    }

    @Name("Debugging")
    @Config.Comment("Debugging options")
    public static Debugging zdebugging = new Debugging();

    public static class Debugging
    {
        @Name("Debug mode")
        @Config.Comment("Enable debug features on this menu, display extra debug info.")
        public boolean debug = false;
        
        @Name("Debug time modifier")
        @Config.Comment("Modify regrow speed in debug mode")
        public double regrowModifier = 1.0;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    private static class EventHandler
    {
        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event
         *            The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Reference.MOD_ID))
            {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}

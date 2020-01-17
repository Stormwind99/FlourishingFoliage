package com.wumple.flourishingfoliage;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

// See
// https://github.com/McJty/YouTubeModding14/blob/master/src/main/java/com/mcjty/mytutorial/Config.java
// https://wiki.mcjty.eu/modding/index.php?title=Tut14_Ep6

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager
{
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec CLIENT_CONFIG;

	public static final String CATEGORY_GENERAL = "General";
	public static final String CATEGORY_DEBUGGING = "Debugging";

	public static class General
	{
		public static ForgeConfigSpec.IntValue leafRegrowthRate;
		public static ForgeConfigSpec.IntValue lightRequiredToGrow;
		public static ForgeConfigSpec.BooleanValue growOutward;
		public static ForgeConfigSpec.IntValue timeToGiveUp;

		private static void setupConfig()
		{
			COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

			// @Name("Leaf regrow rate")
			leafRegrowthRate = COMMON_BUILDER.comment("Seconds between trying or re-trying leaf regrowth.")
					.defineInRange("leafRegrowthRate", 1200, 0, Integer.MAX_VALUE);

			// @Name("Light required to grow")
			lightRequiredToGrow = COMMON_BUILDER
					.comment("Light level required for leaves to start regrowing. 0 = no light required.")
					.defineInRange("lightRequiredToGrow", 9, 0, 15);

			// @Name("Grow outward")
			growOutward = COMMON_BUILDER
					.comment("Must leaves grow next to other leaves or logs (aka \"can sustain leaves\") to regrow")
					.define("growOutward", true);

			// @Name("Time to give up")
			timeToGiveUp = COMMON_BUILDER.comment("Seconds until giving up on regrowth. 0 = never give up.")
					.defineInRange("timeToGiveUp", 100800, 0, Integer.MAX_VALUE);

			COMMON_BUILDER.pop();
		}
	}

	public static class Debugging
	{
		public static ForgeConfigSpec.BooleanValue debug;
		public static ForgeConfigSpec.DoubleValue regrowModifier;
		public static ForgeConfigSpec.BooleanValue showRepairingBlocks;

		
		private static void setupConfig()
		{
			// @Config.Comment("Debugging options")
			COMMON_BUILDER.comment("Debugging settings").push(CATEGORY_DEBUGGING);

			// @Name("Debug mode")
			debug = COMMON_BUILDER.comment("Enable general debug features, display extra debug info").define("debug",
					false);
			
			showRepairingBlocks = COMMON_BUILDER.comment("Make repairing blocks visible").define("showRepairingBlocks",
					false);

			// @Name("Debug time modifier")
			regrowModifier = COMMON_BUILDER.comment("Modify regrow speed in debug mode").defineInRange("regrowModifier",
					1.0F, 0.0F, Double.MAX_VALUE);

			COMMON_BUILDER.pop();
		}
	}

	static
	{
		General.setupConfig();
		Debugging.setupConfig();

		COMMON_CONFIG = COMMON_BUILDER.build();
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path)
	{

		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();

		configData.load();
		spec.setConfig(configData);
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent)
	{
	}

	@SubscribeEvent
	public static void onReload(final ModConfig.ConfigReloading configEvent)
	{
	}

	public static void register(final ModLoadingContext context)
	{
		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
		context.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);

		loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-client.toml"));
		loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-common.toml"));
	}
}
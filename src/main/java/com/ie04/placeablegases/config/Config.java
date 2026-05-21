package com.ie04.placeablegases.config;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.gas.GasProperties;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = PlaceableGasesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final BuiltInGasConfig HYDROGEN = defineGas("hydrogen", 0.07f, 1.0f, 1.00f, 0.00f, 1.00f, 0.90f, 0xFFE8F7FF, 250, 1000, false);
    public static final BuiltInGasConfig OXYGEN = defineGas("oxygen", 1.00f, 1.0f, 0.65f, 0.00f, 0.00f, 0.00f, 0xFFBFD8FF, 300, 1200, false);
    public static final BuiltInGasConfig CHLORINE = defineGas("chlorine", 2.50f, 1.0f, 0.35f, 0.85f, 0.00f, 0.10f, 0xFFB6D85A, 200, 800, true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static BuiltInGasConfig defineGas(String name, float density, float pressureMultiplier, float diffusivity, float toxicity, float flammability, float explosiveness, int color, int safePressure, int maxPressure, boolean reactsWithAir)
    {
        BUILDER.push("gases");
        BUILDER.push(name);
        BuiltInGasConfig config = new BuiltInGasConfig(
                defineFloat("density", density, 0.0, 1000.0, "Relative density compared to air. Values below 1 rise; values above 1 sink."),
                defineFloat("pressureMultiplier", pressureMultiplier, 0.0001, 1000.0, "Pressure produced per mB compared to a normal gas."),
                defineFloat("diffusivity", diffusivity, 0.0, 1000.0, "How readily this gas spreads during simulation."),
                defineFloat("toxicity", toxicity, 0.0, 1.0, "Placeholder toxicity value from 0 to 1."),
                defineFloat("flammability", flammability, 0.0, 1.0, "Placeholder flammability value from 0 to 1."),
                defineFloat("explosiveness", explosiveness, 0.0, 1.0, "Placeholder explosiveness value from 0 to 1."),
                BUILDER.comment("ARGB gas render color, for example 0xFFE8F7FF.")
                        .define("color", colorToString(color), Config::isColorString),
                BUILDER.comment("Pressure considered safe for this gas.")
                        .defineInRange("safePressure", safePressure, 0, Integer.MAX_VALUE),
                BUILDER.comment("Maximum gas-specific pressure tolerance.")
                        .defineInRange("maxPressure", maxPressure, 1, Integer.MAX_VALUE),
                BUILDER.comment("Whether this gas has placeholder air reaction behavior enabled.")
                        .define("reactsWithAir", reactsWithAir)
        );
        BUILDER.pop();
        BUILDER.pop();
        return config;
    }

    private static ForgeConfigSpec.DoubleValue defineFloat(String name, float value, double min, double max, String comment)
    {
        return BUILDER.comment(comment).defineInRange(name, value, min, max);
    }

    @SubscribeEvent
    static void onConfigChanged(final ModConfigEvent event)
    {
        // Built-in gas properties are read directly from ForgeConfigSpec values,
        // so config reloads update live gas behavior without rebuilding the registry.
    }

    private static boolean isColorString(Object value)
    {
        if (!(value instanceof String color))
            return false;

        try
        {
            parseColor(color);
            return true;
        }
        catch (NumberFormatException exception)
        {
            return false;
        }
    }

    private static String colorToString(int color)
    {
        return String.format("0x%08X", color);
    }

    private static int parseColor(String color)
    {
        String normalized = color.trim();
        if (normalized.startsWith("#"))
            normalized = "FF" + normalized.substring(1);
        else if (normalized.startsWith("0x") || normalized.startsWith("0X"))
            normalized = normalized.substring(2);

        return (int) Long.parseLong(normalized, 16);
    }

    public record BuiltInGasConfig(
            ForgeConfigSpec.DoubleValue density,
            ForgeConfigSpec.DoubleValue pressureMultiplier,
            ForgeConfigSpec.DoubleValue diffusivity,
            ForgeConfigSpec.DoubleValue toxicity,
            ForgeConfigSpec.DoubleValue flammability,
            ForgeConfigSpec.DoubleValue explosiveness,
            ForgeConfigSpec.ConfigValue<String> color,
            ForgeConfigSpec.IntValue safePressure,
            ForgeConfigSpec.IntValue maxPressure,
            ForgeConfigSpec.BooleanValue reactsWithAir
    ) {
        public GasProperties getProperties()
        {
            return new GasProperties(
                    density.get().floatValue(),
                    pressureMultiplier.get().floatValue(),
                    diffusivity.get().floatValue(),
                    toxicity.get().floatValue(),
                    flammability.get().floatValue(),
                    explosiveness.get().floatValue(),
                    parseColor(color.get()),
                    safePressure.get(),
                    maxPressure.get(),
                    reactsWithAir.get()
            );
        }
    }
}

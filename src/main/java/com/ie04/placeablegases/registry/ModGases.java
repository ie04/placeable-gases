package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.api.GasApi;
import com.ie04.placeablegases.gas.Gas;
import com.ie04.placeablegases.gas.GasProperties;
import com.ie04.placeablegases.gas.behavior.ChlorineGasBehavior;
import com.ie04.placeablegases.gas.behavior.HydrogenGasBehavior;
import com.ie04.placeablegases.gas.behavior.OxygenGasBehavior;
import net.minecraft.resources.ResourceLocation;

public final class ModGases
{
    private static final float DEFAULT_PRESSURE_MULTIPLIER = 1.0f;

    public static Gas HYDROGEN;
    public static Gas OXYGEN;
    public static Gas CHLORINE;

    private static boolean registered;

    private ModGases()
    {
    }

    public static void register()
    {
        if (registered)
            return;

        HYDROGEN = GasApi.registerGas(id("hydrogen"), ModFluids.HYDROGEN.get(),
                new GasProperties(0.07f, DEFAULT_PRESSURE_MULTIPLIER, 1.00f, 0.00f, 1.00f, 0.90f, 0xFFE8F7FF, 250, 1000, false),
                new HydrogenGasBehavior());

        OXYGEN = GasApi.registerGas(id("oxygen"), ModFluids.OXYGEN.get(),
                new GasProperties(1.10f, DEFAULT_PRESSURE_MULTIPLIER, 0.65f, 0.00f, 0.00f, 0.00f, 0xFFBFD8FF, 300, 1200, false),
                new OxygenGasBehavior());

        CHLORINE = GasApi.registerGas(id("chlorine"), ModFluids.CHLORINE.get(),
                new GasProperties(2.50f, DEFAULT_PRESSURE_MULTIPLIER, 0.35f, 0.85f, 0.00f, 0.10f, 0xFFB6D85A, 200, 800, true),
                new ChlorineGasBehavior());

        registered = true;
    }

    private static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(PlaceableGasesMod.MODID, path);
    }
}

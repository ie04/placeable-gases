package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.api.GasApi;
import com.ie04.placeablegases.config.Config;
import com.ie04.placeablegases.gas.Gas;
import com.ie04.placeablegases.gas.behavior.ChlorineGasBehavior;
import com.ie04.placeablegases.gas.behavior.HydrogenGasBehavior;
import com.ie04.placeablegases.gas.behavior.OxygenGasBehavior;
import net.minecraft.resources.ResourceLocation;

public final class ModGases
{
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
                Config.HYDROGEN::getProperties,
                new HydrogenGasBehavior());

        OXYGEN = GasApi.registerGas(id("oxygen"), ModFluids.OXYGEN.get(),
                Config.OXYGEN::getProperties,
                new OxygenGasBehavior());

        CHLORINE = GasApi.registerGas(id("chlorine"), ModFluids.CHLORINE.get(),
                Config.CHLORINE::getProperties,
                new ChlorineGasBehavior());

        registered = true;
    }

    private static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(PlaceableGasesMod.MODID, path);
    }
}

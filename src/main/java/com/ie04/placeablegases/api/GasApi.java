package com.ie04.placeablegases.api;

import com.ie04.placeablegases.gas.Gas;
import com.ie04.placeablegases.gas.GasBehavior;
import com.ie04.placeablegases.gas.GasProperties;
import com.ie04.placeablegases.gas.GasRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public final class GasApi
{
    private GasApi()
    {
    }

    public static Gas registerGas(ResourceLocation id, Fluid fluid, GasProperties properties, GasBehavior behavior)
    {
        return GasRegistry.register(new Gas(id, fluid, properties, behavior));
    }

    public static Optional<Gas> getGas(ResourceLocation id)
    {
        return GasRegistry.get(id);
    }

    public static Optional<Gas> getGas(Fluid fluid)
    {
        return GasRegistry.get(fluid);
    }

    public static boolean isGas(Fluid fluid)
    {
        return GasRegistry.isGas(fluid);
    }
}

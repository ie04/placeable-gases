package com.ie04.placeablegases.gas;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class GasRegistry
{
    private static final Map<ResourceLocation, Gas> BY_ID = new LinkedHashMap<>();
    private static final IdentityHashMap<Fluid, Gas> BY_FLUID = new IdentityHashMap<>();

    private GasRegistry()
    {
    }

    public static Gas register(Gas gas)
    {
        Objects.requireNonNull(gas, "gas");

        if (BY_ID.containsKey(gas.getId()))
            throw new IllegalArgumentException("Duplicate gas id: " + gas.getId());

        Gas existingFluidGas = BY_FLUID.get(gas.getFluid());
        if (existingFluidGas != null)
            throw new IllegalArgumentException("Fluid " + gas.getFluid() + " is already registered as gas " + existingFluidGas.getId());

        BY_ID.put(gas.getId(), gas);
        BY_FLUID.put(gas.getFluid(), gas);
        return gas;
    }

    public static Optional<Gas> get(ResourceLocation id)
    {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static Optional<Gas> get(Fluid fluid)
    {
        return Optional.ofNullable(BY_FLUID.get(fluid));
    }

    public static boolean isGas(Fluid fluid)
    {
        return BY_FLUID.containsKey(fluid);
    }

    public static Collection<Gas> all()
    {
        return Collections.unmodifiableCollection(BY_ID.values());
    }
}

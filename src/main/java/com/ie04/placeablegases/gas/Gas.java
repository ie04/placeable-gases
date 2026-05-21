package com.ie04.placeablegases.gas;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

/**
 * Defines one gas type.
 *
 * Forge Fluid is kept as the storage/interoperability handle, while Gas owns
 * custom atmospheric behavior that does not fit Minecraft's fluid model.
 */
public class Gas
{
    private final ResourceLocation id;
    private final Fluid fluid;
    private final GasProperties properties;
    private final GasBehavior behavior;

    public Gas(ResourceLocation id, Fluid fluid, GasProperties properties, GasBehavior behavior)
    {
        this.id = Objects.requireNonNull(id, "id");
        this.fluid = Objects.requireNonNull(fluid, "fluid");
        this.properties = Objects.requireNonNull(properties, "properties");
        this.behavior = Objects.requireNonNull(behavior, "behavior");
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    public GasProperties getProperties()
    {
        return properties;
    }

    public GasBehavior getBehavior()
    {
        return behavior;
    }
}

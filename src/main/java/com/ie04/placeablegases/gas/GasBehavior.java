package com.ie04.placeablegases.gas;

import net.minecraft.world.entity.Entity;

public interface GasBehavior
{
    default void onReleased(GasReleaseContext context)
    {
    }

    default void onCloudTick(GasCloudTickContext context)
    {
    }

    default void onEntityInside(GasStack stack, Entity entity)
    {
    }

    default boolean canReactWithAir(GasReactionContext context)
    {
        return false;
    }

    default ReactionResult reactWithAir(GasReactionContext context)
    {
        return ReactionResult.none(context.stack());
    }

    default boolean canIgnite(GasIgnitionContext context)
    {
        return false;
    }

    default void onIgnited(GasIgnitionContext context)
    {
    }
}

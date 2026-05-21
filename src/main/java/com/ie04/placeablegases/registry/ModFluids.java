package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, PlaceableGasesMod.MODID);

    // TODO: Replace these inert placeholders with real Forge fluid implementations when storage/tank support is added.
    public static final RegistryObject<Fluid> HYDROGEN = FLUIDS.register("hydrogen", PlaceholderGasFluid::new);
    public static final RegistryObject<Fluid> OXYGEN = FLUIDS.register("oxygen", PlaceholderGasFluid::new);
    public static final RegistryObject<Fluid> CHLORINE = FLUIDS.register("chlorine", PlaceholderGasFluid::new);

    private ModFluids()
    {
    }

    public static void register(IEventBus eventBus)
    {
        FLUIDS.register(eventBus);
    }

    private static class PlaceholderGasFluid extends Fluid
    {
        @Override
        public Item getBucket()
        {
            return Items.AIR;
        }

        @Override
        protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction)
        {
            return false;
        }

        @Override
        protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state)
        {
            return Vec3.ZERO;
        }

        @Override
        public int getTickDelay(LevelReader level)
        {
            return 0;
        }

        @Override
        protected float getExplosionResistance()
        {
            return 0.0f;
        }

        @Override
        public float getHeight(FluidState state, BlockGetter level, BlockPos pos)
        {
            return 0.0f;
        }

        @Override
        public float getOwnHeight(FluidState state)
        {
            return 0.0f;
        }

        @Override
        protected BlockState createLegacyBlock(FluidState state)
        {
            return Blocks.AIR.defaultBlockState();
        }

        @Override
        public boolean isSource(FluidState state)
        {
            return false;
        }

        @Override
        public int getAmount(FluidState state)
        {
            return 0;
        }

        @Override
        protected boolean isEmpty()
        {
            return true;
        }

        @Override
        protected ParticleOptions getDripParticle()
        {
            return null;
        }

        @Override
        public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos)
        {
            return Shapes.empty();
        }

        @Override
        public java.util.Optional<SoundEvent> getPickupSound()
        {
            return java.util.Optional.empty();
        }
    }
}

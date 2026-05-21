package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.block.GasVoxelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PlaceableGasesMod.MODID);

    public static final RegistryObject<Block> GAS_VOXEL = BLOCKS.register("gas_voxel", () -> new GasVoxelBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NONE)
            .noCollission()
            .noOcclusion()
            .air()
            .strength(0.0f)));

    private ModBlocks()
    {
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}

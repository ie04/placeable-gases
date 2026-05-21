package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.block.entity.GasVoxelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PlaceableGasesMod.MODID);

    public static final RegistryObject<BlockEntityType<GasVoxelBlockEntity>> GAS_VOXEL = BLOCK_ENTITIES.register("gas_voxel", () -> BlockEntityType.Builder
            .of(GasVoxelBlockEntity::new, ModBlocks.GAS_VOXEL.get())
            .build(null));

    private ModBlockEntities()
    {
    }

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}

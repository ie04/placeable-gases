package com.ie04.placeablegases.block;

import com.ie04.placeablegases.block.entity.GasVoxelBlockEntity;
import com.ie04.placeablegases.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class GasVoxelBlock extends BaseEntityBlock
{
    public GasVoxelBlock(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new GasVoxelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
    {
        if (level.isClientSide || blockEntityType != ModBlockEntities.GAS_VOXEL.get())
            return null;

        return (tickerLevel, pos, tickerState, blockEntity) -> GasVoxelBlockEntity.serverTick((ServerLevel) tickerLevel, pos, tickerState, (GasVoxelBlockEntity) blockEntity);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }
}

package com.ie04.placeablegases.simulation;

import com.ie04.placeablegases.block.entity.GasVoxelBlockEntity;
import com.ie04.placeablegases.gas.Gas;
import com.ie04.placeablegases.gas.GasStack;
import com.ie04.placeablegases.pressure.AmbientPressure;
import com.ie04.placeablegases.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class GasCloudSimulator
{
    public static final int INITIAL_SPREAD_PASSES = 48;
    private static final int MAX_TRANSFERS_PER_TICK = 4;
    private static final int MIN_TRANSFER_UNITS = 8;
    private static final float AIR_DENSITY = 1.0f;

    private GasCloudSimulator()
    {
    }

    public static boolean release(ServerLevel level, BlockPos pos, GasStack gasStack)
    {
        return addGasToCell(level, pos, gasStack, INITIAL_SPREAD_PASSES);
    }

    public static void tick(ServerLevel level, BlockPos pos, GasVoxelBlockEntity source)
    {
        if (source.getTotalGasUnits() <= 0)
        {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        if (tryBuoyancyMove(level, pos, source))
            return;

        if (source.getSpreadPasses() > 0)
            spread(level, pos, source);
    }

    private static void spread(ServerLevel level, BlockPos pos, GasVoxelBlockEntity source)
    {
        int transfers = 0;
        for (Direction direction : orderedDirections(source.getWeightedDensity()))
        {
            if (transfers >= MAX_TRANSFERS_PER_TICK || source.getTotalGasUnits() <= MIN_TRANSFER_UNITS)
                break;

            if (transferToward(level, pos, source, direction))
                transfers++;
        }

        source.setSpreadPasses(source.getSpreadPasses() - 1);
    }

    private static boolean tryBuoyancyMove(ServerLevel level, BlockPos sourcePos, GasVoxelBlockEntity source)
    {
        Direction buoyancyDirection = buoyancyDirection(source.getWeightedDensity());
        if (buoyancyDirection == null)
            return false;

        BlockPos targetPos = sourcePos.relative(buoyancyDirection);
        if (!canOccupy(level, targetPos))
            return false;

        GasVoxelBlockEntity target = getOrCreateTarget(level, targetPos, source.getSpreadPasses());
        if (target == null)
            return false;

        List<Map.Entry<Gas, Integer>> gasEntries = new ArrayList<>(source.getGases().entrySet());
        for (Map.Entry<Gas, Integer> entry : gasEntries)
        {
            target.addGas(entry.getKey(), entry.getValue(), source.getSpreadPasses());
            source.removeGas(entry.getKey(), entry.getValue());
        }
        target.setAirUnits(source.getAirUnits());

        level.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), 3);
        return true;
    }

    private static boolean transferToward(ServerLevel level, BlockPos sourcePos, GasVoxelBlockEntity source, Direction direction)
    {
        BlockPos targetPos = sourcePos.relative(direction);
        if (!canOccupy(level, targetPos))
            return false;

        GasVoxelBlockEntity target = getOrCreateTarget(level, targetPos, Math.max(0, source.getSpreadPasses() - 1));
        if (target == null)
            return false;

        int movedAny = 0;
        List<Map.Entry<Gas, Integer>> gasEntries = new ArrayList<>(source.getGases().entrySet());
        for (Map.Entry<Gas, Integer> entry : gasEntries)
        {
            int transferAmount = Math.max(MIN_TRANSFER_UNITS, entry.getValue() / 4);
            transferAmount = Math.min(transferAmount, entry.getValue());
            if (transferAmount <= 0)
                continue;

            source.removeGas(entry.getKey(), transferAmount);
            target.addGas(entry.getKey(), transferAmount, Math.max(0, source.getSpreadPasses() - 1));
            movedAny += transferAmount;
        }

        return movedAny > 0;
    }

    private static GasVoxelBlockEntity getOrCreateTarget(ServerLevel level, BlockPos pos, int spreadPasses)
    {
        BlockEntity existingBlockEntity = level.getBlockEntity(pos);
        if (existingBlockEntity instanceof GasVoxelBlockEntity gasVoxel)
            return gasVoxel;

        if (!level.getBlockState(pos).isAir())
            return null;

        level.setBlock(pos, ModBlocks.GAS_VOXEL.get().defaultBlockState(), 3);
        BlockEntity createdBlockEntity = level.getBlockEntity(pos);
        if (createdBlockEntity instanceof GasVoxelBlockEntity gasVoxel)
        {
            gasVoxel.setAirUnits(AmbientPressure.airUnitsAtY(pos.getY()));
            gasVoxel.setSpreadPasses(spreadPasses);
            return gasVoxel;
        }

        return null;
    }

    private static boolean addGasToCell(ServerLevel level, BlockPos pos, GasStack gasStack, int spreadPasses)
    {
        GasVoxelBlockEntity target = getOrCreateTarget(level, pos, spreadPasses);
        if (target == null)
            return false;

        target.addGas(gasStack, spreadPasses);
        return true;
    }

    private static boolean canOccupy(ServerLevel level, BlockPos pos)
    {
        return level.getBlockState(pos).isAir() || level.getBlockEntity(pos) instanceof GasVoxelBlockEntity;
    }

    private static List<Direction> orderedDirections(float density)
    {
        List<Direction> directions = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN));
        directions.sort(Comparator.comparingInt(direction -> priority(direction, density)));
        return directions;
    }

    private static int priority(Direction direction, float density)
    {
        if (density < AIR_DENSITY && direction == Direction.UP)
            return 0;
        if (density > AIR_DENSITY && direction == Direction.DOWN)
            return 0;
        if (direction.getAxis().isHorizontal())
            return 1;
        return 2;
    }

    private static Direction buoyancyDirection(float density)
    {
        if (density < AIR_DENSITY)
            return Direction.UP;
        if (density > AIR_DENSITY)
            return Direction.DOWN;
        return null;
    }
}

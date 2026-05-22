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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GasCloudSimulator
{
    public static final int INITIAL_SPREAD_PASSES = 48;

    /**
     * If a voxel contains this much gas or less, it no longer actively expands.
     * Example: 500 mB means gas spreads until each active voxel is roughly <= 500 mB.
     */
    private static final int SPREAD_THRESHOLD_UNITS = 500;

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

        if (source.getSpreadPasses() <= 0)
            return;

        if (source.getTotalGasUnits() <= SPREAD_THRESHOLD_UNITS)
            return;

        spreadEqualized(level, pos, source);
    }

    /**
     * Equalizes gas between:
     * - the source voxel
     * - all adjacent air blocks
     * - all adjacent gas voxel blocks
     *
     * Existing gas in adjacent gas voxels is included in the total before redistribution.
     */
    private static void spreadEqualized(ServerLevel level, BlockPos sourcePos, GasVoxelBlockEntity source)
    {
        int nextSpreadPasses = Math.max(0, source.getSpreadPasses() - 1);

        List<BlockPos> targetPositions = getSpreadTargets(level, sourcePos);

        if (targetPositions.size() <= 1)
        {
            source.setSpreadPasses(nextSpreadPasses);
            return;
        }

        List<GasVoxelBlockEntity> targetCells = new ArrayList<>();

        for (BlockPos targetPos : targetPositions)
        {
            GasVoxelBlockEntity cell = getOrCreateTarget(level, targetPos, nextSpreadPasses);

            if (cell != null)
                targetCells.add(cell);
        }

        if (targetCells.size() <= 1)
        {
            source.setSpreadPasses(nextSpreadPasses);
            return;
        }

        Set<Gas> gasesToEqualize = collectGasTypes(targetCells);

        for (Gas gas : gasesToEqualize)
        {
            int totalGas = 0;

            for (GasVoxelBlockEntity cell : targetCells)
                totalGas += getGasUnits(cell, gas);

            int equalAmount = totalGas / targetCells.size();
            int remainder = totalGas % targetCells.size();

            for (int i = 0; i < targetCells.size(); i++)
            {
                GasVoxelBlockEntity cell = targetCells.get(i);

                int amount = equalAmount;

                // Preserve exact total amount despite integer division.
                if (i < remainder)
                    amount++;

                setGasUnits(cell, gas, amount, nextSpreadPasses);
            }
        }

        for (GasVoxelBlockEntity cell : targetCells)
            cell.setSpreadPasses(nextSpreadPasses);
    }

    private static List<BlockPos> getSpreadTargets(ServerLevel level, BlockPos sourcePos)
    {
        List<BlockPos> targets = new ArrayList<>();
        targets.add(sourcePos);

        for (Direction direction : Direction.values())
        {
            BlockPos neighborPos = sourcePos.relative(direction);

            if (canOccupy(level, neighborPos))
                targets.add(neighborPos);
        }

        return targets;
    }

    private static Set<Gas> collectGasTypes(List<GasVoxelBlockEntity> cells)
    {
        Set<Gas> gases = new HashSet<>();

        for (GasVoxelBlockEntity cell : cells)
            gases.addAll(cell.getGases().keySet());

        return gases;
    }

    private static int getGasUnits(GasVoxelBlockEntity cell, Gas gas)
    {
        return cell.getGases().getOrDefault(gas, 0);
    }

    private static void setGasUnits(GasVoxelBlockEntity cell, Gas gas, int targetAmount, int spreadPasses)
    {
        int currentAmount = getGasUnits(cell, gas);

        if (targetAmount > currentAmount)
        {
            cell.addGas(gas, targetAmount - currentAmount, spreadPasses);
        }
        else if (targetAmount < currentAmount)
        {
            cell.removeGas(gas, currentAmount - targetAmount);
        }
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
        return level.getBlockState(pos).isAir()
                || level.getBlockEntity(pos) instanceof GasVoxelBlockEntity;
    }
}
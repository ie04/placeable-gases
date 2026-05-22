package com.ie04.placeablegases.block.entity;

import com.ie04.placeablegases.api.GasApi;
import com.ie04.placeablegases.gas.Gas;
import com.ie04.placeablegases.gas.GasStack;
import com.ie04.placeablegases.pressure.AmbientPressure;
import com.ie04.placeablegases.registry.ModBlockEntities;
import com.ie04.placeablegases.simulation.GasCloudSimulator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;

public class GasVoxelBlockEntity extends BlockEntity
{
    private static final String AIR_UNITS_KEY = "AirUnits";
    private static final String SPREAD_PASSES_KEY = "SpreadPasses";
    private static final String GASES_KEY = "Gases";
    private static final String GAS_ID_KEY = "GasId";
    private static final String AMOUNT_KEY = "Amount";

    private final Map<Gas, Integer> gases = new LinkedHashMap<>();
    private int airUnits;
    private int spreadPasses;

    public GasVoxelBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.GAS_VOXEL.get(), pos, state);
        this.airUnits = AmbientPressure.airUnitsAtY(pos.getY());
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, GasVoxelBlockEntity blockEntity)
    {
        GasCloudSimulator.tick(level, pos, blockEntity);
    }

    public int getAirUnits()
    {
        return airUnits;
    }

    public void setAirUnits(int airUnits)
    {
        this.airUnits = Math.max(0, airUnits);
        markChangedAndSync();
    }

    public int getSpreadPasses()
    {
        return spreadPasses;
    }

    public void setSpreadPasses(int spreadPasses)
    {
        this.spreadPasses = Math.max(0, spreadPasses);
        markChangedAndSync();
    }

    public int getTotalGasUnits()
    {
        return gases.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Gas, Integer> getGases()
    {
        return gases;
    }

    public void addGas(GasStack gasStack, int passes)
    {
        addGas(gasStack.getGas(), gasStack.getAmount(), passes);
    }

    public void addGas(Gas gas, int amount, int passes)
    {
        if (amount <= 0)
            return;

        gases.merge(gas, amount, Integer::sum);
        spreadPasses = Math.max(spreadPasses, Math.max(0, passes));
        markChangedAndSync();
    }

    public int getGasUnits(Gas gas)
    {
        return gases.getOrDefault(gas, 0);
    }

    public void removeGas(Gas gas, int amount)
    {
        int remaining = gases.getOrDefault(gas, 0) - Math.max(0, amount);
        if (remaining > 0)
            gases.put(gas, remaining);
        else
            gases.remove(gas);
        markChangedAndSync();
    }

    public float getWeightedDensity()
    {
        int totalGas = getTotalGasUnits();
        if (totalGas <= 0)
            return 1.0f;

        float weightedDensity = 0.0f;
        for (Map.Entry<Gas, Integer> entry : gases.entrySet())
            weightedDensity += entry.getKey().getProperties().density() * entry.getValue();

        return weightedDensity / totalGas;
    }

    public float getGasOccupancy()
    {
        int totalGas = getTotalGasUnits();
        if (totalGas <= 0)
            return 0.0f;

        return Math.min(1.0f, totalGas / (float) Math.max(1, totalGas + airUnits));
    }

    public int getWeightedColor()
    {
        int totalGas = getTotalGasUnits();
        if (totalGas <= 0)
            return 0xFFFFFFFF;

        int red = 0;
        int green = 0;
        int blue = 0;
        for (Map.Entry<Gas, Integer> entry : gases.entrySet())
        {
            int color = entry.getKey().getProperties().color();
            int amount = entry.getValue();
            red += ((color >> 16) & 0xFF) * amount;
            green += ((color >> 8) & 0xFF) * amount;
            blue += (color & 0xFF) * amount;
        }

        return 0xFF000000
                | ((red / totalGas) << 16)
                | ((green / totalGas) << 8)
                | (blue / totalGas);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putInt(AIR_UNITS_KEY, airUnits);
        tag.putInt(SPREAD_PASSES_KEY, spreadPasses);

        ListTag gasList = new ListTag();
        for (Map.Entry<Gas, Integer> entry : gases.entrySet())
        {
            CompoundTag gasTag = new CompoundTag();
            gasTag.putString(GAS_ID_KEY, entry.getKey().getId().toString());
            gasTag.putInt(AMOUNT_KEY, entry.getValue());
            gasList.add(gasTag);
        }
        tag.put(GASES_KEY, gasList);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        airUnits = Math.max(0, tag.getInt(AIR_UNITS_KEY));
        spreadPasses = Math.max(0, tag.getInt(SPREAD_PASSES_KEY));
        gases.clear();

        ListTag gasList = tag.getList(GASES_KEY, Tag.TAG_COMPOUND);
        for (Tag listEntry : gasList)
        {
            CompoundTag gasTag = (CompoundTag) listEntry;
            ResourceLocation gasId = ResourceLocation.tryParse(gasTag.getString(GAS_ID_KEY));
            if (gasId == null)
                continue;

            int amount = Math.max(0, gasTag.getInt(AMOUNT_KEY));
            GasApi.getGas(gasId).ifPresent(gas -> {
                if (amount > 0)
                    gases.put(gas, amount);
            });
        }
    }

    private void markChangedAndSync()
    {
        setChanged();
        if (level != null && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
}

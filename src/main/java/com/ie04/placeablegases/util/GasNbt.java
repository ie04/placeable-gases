package com.ie04.placeablegases.util;

import com.ie04.placeablegases.api.GasApi;
import com.ie04.placeablegases.gas.GasStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class GasNbt
{
    private static final String GAS_ID = "GasId";
    private static final String AMOUNT = "Amount";
    private static final String PRESSURE = "Pressure";
    private static final String TEMPERATURE = "Temperature";
    private static final String PURITY = "Purity";

    private GasNbt()
    {
    }

    public static boolean hasGas(ItemStack stack)
    {
        return getGasStack(stack).isPresent();
    }

    public static Optional<GasStack> getGasStack(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(GAS_ID) || !tag.contains(AMOUNT))
            return Optional.empty();

        ResourceLocation gasId = ResourceLocation.tryParse(tag.getString(GAS_ID));
        if (gasId == null)
            return Optional.empty();

        int amount = Math.max(0, tag.getInt(AMOUNT));
        if (amount <= 0)
            return Optional.empty();

        int pressure = Math.max(0, tag.getInt(PRESSURE));
        int temperature = tag.getInt(TEMPERATURE);
        int purity = clampPurity(tag.getInt(PURITY));

        return GasApi.getGas(gasId)
                .map(gas -> new GasStack(gas, amount, pressure, temperature, purity));
    }

    public static void setGasStack(ItemStack stack, GasStack gasStack)
    {
        if (gasStack.getAmount() <= 0)
        {
            clearGas(stack);
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(GAS_ID, gasStack.getGas().getId().toString());
        tag.putInt(AMOUNT, Math.max(0, gasStack.getAmount()));
        tag.putInt(PRESSURE, Math.max(0, gasStack.getPressure()));
        tag.putInt(TEMPERATURE, gasStack.getTemperature());
        tag.putInt(PURITY, clampPurity(gasStack.getPurity()));
    }

    public static void clearGas(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return;

        tag.remove(GAS_ID);
        tag.remove(AMOUNT);
        tag.remove(PRESSURE);
        tag.remove(TEMPERATURE);
        tag.remove(PURITY);
        if (tag.isEmpty())
            stack.setTag(null);
    }

    public static int getAmount(ItemStack stack)
    {
        return getGasStack(stack)
                .map(GasStack::getAmount)
                .orElse(0);
    }

    public static void setAmount(ItemStack stack, int amount)
    {
        int clampedAmount = Math.max(0, amount);
        if (clampedAmount <= 0)
        {
            clearGas(stack);
            return;
        }

        getGasStack(stack).ifPresent(gasStack -> {
            gasStack.setAmount(clampedAmount);
            setGasStack(stack, gasStack);
        });
    }

    public static void shrinkAmount(ItemStack stack, int amount)
    {
        int shrinkBy = Math.max(0, amount);
        if (shrinkBy == 0)
            return;

        getGasStack(stack).ifPresent(gasStack -> setAmount(stack, gasStack.getAmount() - shrinkBy));
    }

    public static void recalculatePressure(ItemStack stack, PressureCalculator calculator)
    {
        getGasStack(stack).ifPresent(gasStack -> {
            int pressure = Math.max(0, calculator.calculate(gasStack, gasStack.getAmount(), gasStack.getTemperature()));
            gasStack.setPressure(pressure);
            setGasStack(stack, gasStack);
        });
    }

    @FunctionalInterface
    public interface PressureCalculator
    {
        int calculate(GasStack gasStack, int amount, int temperature);
    }

    private static int clampPurity(int purity)
    {
        return Math.max(0, Math.min(100, purity));
    }
}

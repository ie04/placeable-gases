package com.ie04.placeablegases.item;

import com.ie04.placeablegases.gas.GasStack;
import com.ie04.placeablegases.simulation.GasCloudSimulator;
import com.ie04.placeablegases.util.GasNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class GasCanisterItem extends Item
{
    private static final int DEBUG_RELEASE_AMOUNT = 100;
    private static final int REFERENCE_TEMPERATURE = 295;
    private static final int STANDARD_PRESSURE = 100;

    private final int volumeCapacity;
    private final int maxPressure;

    public GasCanisterItem(Properties properties, int volumeCapacity, int maxPressure)
    {
        super(properties);
        if (volumeCapacity <= 0)
            throw new IllegalArgumentException("Canister volume capacity must be positive.");
        if (maxPressure <= 0)
            throw new IllegalArgumentException("Canister max pressure must be positive.");

        this.volumeCapacity = volumeCapacity;
        this.maxPressure = maxPressure;
    }

    public int getVolumeCapacity()
    {
        return volumeCapacity;
    }

    public int getMaxPressure()
    {
        return maxPressure;
    }

    /**
     * Inserts as much of the incoming gas as this canister can safely hold.
     * This is the foundational pressurization hook for future compressors.
     */
    public int pressurize(ItemStack canisterStack, GasStack incomingGas)
    {
        if (incomingGas.getAmount() <= 0)
            return 0;

        Optional<GasStack> currentGas = GasNbt.getGasStack(canisterStack);
        if (currentGas.isPresent() && currentGas.get().getGas() != incomingGas.getGas())
            return 0;

        GasStack existingGas = currentGas.orElse(null);
        int existingAmount = existingGas == null ? 0 : existingGas.getAmount();
        int acceptedAmount = getAcceptedAmount(existingAmount, incomingGas);
        if (acceptedAmount <= 0)
            return 0;

        int combinedAmount = existingAmount + acceptedAmount;
        int combinedTemperature = combineWeighted(existingGas == null ? 0 : existingGas.getTemperature(), existingAmount, incomingGas.getTemperature(), acceptedAmount);
        int combinedPurity = combineWeighted(existingGas == null ? 0 : existingGas.getPurity(), existingAmount, incomingGas.getPurity(), acceptedAmount);
        int combinedPressure = calculatePressure(incomingGas, combinedAmount, combinedTemperature);

        GasNbt.setGasStack(canisterStack, new GasStack(incomingGas.getGas(), combinedAmount, combinedPressure, combinedTemperature, combinedPurity));
        return acceptedAmount;
    }

    public int calculatePressure(GasStack gasStack, int amount, int temperature)
    {
        if (amount <= 0)
            return 0;

        float temperatureFactor = Math.max(1, temperature) / (float) REFERENCE_TEMPERATURE;
        float pressureMultiplier = gasStack.getGas().getProperties().pressureMultiplier();
        return (int) Math.ceil(amount * pressureMultiplier * temperatureFactor * STANDARD_PRESSURE / volumeCapacity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        Optional<GasStack> gasStack = GasNbt.getGasStack(stack);

        if (gasStack.isEmpty())
        {
            if (!level.isClientSide)
                player.sendSystemMessage(Component.literal("The canister is empty."));

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (!level.isClientSide)
        {
            GasStack storedGas = gasStack.get();
            int releasedAmount = Math.min(DEBUG_RELEASE_AMOUNT, storedGas.getAmount());
            BlockPos releasePos = getReleasePos(level, player);

            if (!(level instanceof ServerLevel serverLevel) || !releaseGasAt(serverLevel, releasePos, stack, storedGas, releasedAmount))
            {
                player.sendSystemMessage(Component.literal("There is no space to release gas."));
                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            int remainingAmount = GasNbt.getAmount(stack);
            player.sendSystemMessage(Component.literal("Released " + releasedAmount + " units of " + storedGas.getGas().getId() + " into a gas cloud at " + releasePos.toShortString() + "."));
            player.sendSystemMessage(Component.literal("Remaining: " + remainingAmount + " units."));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown())
            return super.useOn(context);

        ItemStack stack = context.getItemInHand();
        Optional<GasStack> gasStack = GasNbt.getGasStack(stack);
        Level level = context.getLevel();

        if (gasStack.isEmpty())
        {
            if (!level.isClientSide)
                player.sendSystemMessage(Component.literal("The canister is empty."));

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide)
        {
            GasStack storedGas = gasStack.get();
            BlockPos releasePos = context.getClickedPos().relative(context.getClickedFace());
            if (!(level instanceof ServerLevel serverLevel) || !releaseGasAt(serverLevel, releasePos, stack, storedGas, storedGas.getAmount()))
            {
                player.sendSystemMessage(Component.literal("There is no space to dump gas."));
                return InteractionResult.SUCCESS;
            }

            player.sendSystemMessage(Component.literal("Dumped " + storedGas.getAmount() + " units of " + storedGas.getGas().getId() + " into a gas cloud at " + releasePos.toShortString() + "."));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        Optional<GasStack> gasStack = GasNbt.getGasStack(stack);
        if (gasStack.isEmpty())
        {
            tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Volume: " + volumeCapacity + " units").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Max Pressure: " + maxPressure).withStyle(ChatFormatting.GRAY));
            return;
        }

        GasStack storedGas = gasStack.get();
        tooltip.add(Component.literal("Gas: " + storedGas.getGas().getId()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Amount: " + storedGas.getAmount()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Pressure: " + storedGas.getPressure() + " / " + maxPressure).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Volume: " + volumeCapacity + " units").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Temperature: " + storedGas.getTemperature() + " K").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Purity: " + storedGas.getPurity() + "%").withStyle(ChatFormatting.GRAY));
    }

    private int getAcceptedAmount(int existingAmount, GasStack incomingGas)
    {
        int low = 0;
        int high = incomingGas.getAmount();

        while (low < high)
        {
            int candidate = (low + high + 1) / 2;
            int pressure = calculatePressure(incomingGas, existingAmount + candidate, incomingGas.getTemperature());
            if (pressure <= maxPressure)
                low = candidate;
            else
                high = candidate - 1;
        }

        return low;
    }

    private static int combineWeighted(int existingValue, int existingAmount, int incomingValue, int incomingAmount)
    {
        int totalAmount = existingAmount + incomingAmount;
        if (totalAmount <= 0)
            return incomingValue;

        return Math.round((existingValue * existingAmount + incomingValue * incomingAmount) / (float) totalAmount);
    }

    private static BlockPos getReleasePos(Level level, Player player)
    {
        BlockPos forwardPos = player.blockPosition().relative(player.getDirection());
        if (level.getBlockState(forwardPos).isAir() || level.getBlockEntity(forwardPos) != null)
            return forwardPos;

        return player.blockPosition().above();
    }

    private boolean releaseGasAt(ServerLevel level, BlockPos releasePos, ItemStack canisterStack, GasStack storedGas, int amount)
    {
        int releasedAmount = Math.min(amount, storedGas.getAmount());
        if (releasedAmount <= 0)
            return false;

        GasStack releasedGas = new GasStack(storedGas.getGas(), releasedAmount, storedGas.getPressure(), storedGas.getTemperature(), storedGas.getPurity());
        if (!GasCloudSimulator.release(level, releasePos, releasedGas))
            return false;

        GasNbt.shrinkAmount(canisterStack, releasedAmount);
        GasNbt.recalculatePressure(canisterStack, this::calculatePressure);
        return true;
    }
}

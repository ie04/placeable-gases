package com.ie04.placeablegases.item;

import com.ie04.placeablegases.gas.GasStack;
import com.ie04.placeablegases.util.GasNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class GasCanisterItem extends Item
{
    private static final int DEBUG_RELEASE_AMOUNT = 100;

    public GasCanisterItem(Properties properties)
    {
        super(properties);
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

            // Temporary debug release action. Gas clouds will be created by a later simulation milestone.
            GasNbt.shrinkAmount(stack, releasedAmount);

            int remainingAmount = GasNbt.getAmount(stack);
            player.sendSystemMessage(Component.literal("Released " + releasedAmount + " units of " + storedGas.getGas().getId() + " at " + storedGas.getPressure() + " pressure."));
            player.sendSystemMessage(Component.literal("Remaining: " + remainingAmount + " units."));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        Optional<GasStack> gasStack = GasNbt.getGasStack(stack);
        if (gasStack.isEmpty())
        {
            tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        GasStack storedGas = gasStack.get();
        tooltip.add(Component.literal("Gas: " + storedGas.getGas().getId()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Amount: " + storedGas.getAmount()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Pressure: " + storedGas.getPressure()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Temperature: " + storedGas.getTemperature() + " K").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Purity: " + storedGas.getPurity() + "%").withStyle(ChatFormatting.GRAY));
    }
}

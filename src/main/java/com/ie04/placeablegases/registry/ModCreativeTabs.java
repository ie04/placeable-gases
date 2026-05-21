package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.gas.Gas;
import com.ie04.placeablegases.gas.GasStack;
import com.ie04.placeablegases.item.GasCanisterItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PlaceableGasesMod.MODID);

    public static final RegistryObject<CreativeModeTab> PLACEABLE_GASES = CREATIVE_MODE_TABS.register("placeable_gases", () -> CreativeModeTab.builder()
            .title(Component.literal("Placeable Gases"))
            .icon(() -> new ItemStack(ModItems.GAS_CANISTER.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.GAS_CANISTER.get());
                output.accept(prefilledCanister(ModGases.HYDROGEN, 1000, 1200, 295, 100));
                output.accept(prefilledCanister(ModGases.OXYGEN, 1000, 1000, 295, 100));
                output.accept(prefilledCanister(ModGases.CHLORINE, 1000, 1100, 295, 98));
            }).build());

    private ModCreativeTabs()
    {
    }

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    private static ItemStack prefilledCanister(Gas gas, int amount, int startingPressure, int temperature, int purity)
    {
        ItemStack stack = new ItemStack(ModItems.GAS_CANISTER.get());
        if (gas != null && stack.getItem() instanceof GasCanisterItem gasCanister)
            gasCanister.pressurize(stack, new GasStack(gas, amount, startingPressure, temperature, purity));
        return stack;
    }
}

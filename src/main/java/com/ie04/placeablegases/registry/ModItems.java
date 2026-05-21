package com.ie04.placeablegases.registry;

import com.ie04.placeablegases.PlaceableGasesMod;
import com.ie04.placeablegases.item.GasCanisterItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PlaceableGasesMod.MODID);
    public static final int GAS_CANISTER_VOLUME_CAPACITY = 1000;
    public static final int GAS_CANISTER_MAX_PRESSURE = 1200;

    public static final RegistryObject<Item> GAS_CANISTER = ITEMS.register("gas_canister", () -> new GasCanisterItem(new Item.Properties().stacksTo(1), GAS_CANISTER_VOLUME_CAPACITY, GAS_CANISTER_MAX_PRESSURE));

    private ModItems()
    {
    }

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}

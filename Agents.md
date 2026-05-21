# Agents.md

Project notes for coding agents working on Placeable Gases Mod.

- Project: Forge 1.20.1 mod named Placeable Gases Mod.
- Mod id: `placeablegases`.
- Author: Iyad Eltifi (`ie04`).
- Base Java package: `com.ie04.placeablegases`.
- Main mod class: `PlaceableGasesMod`.
- Git repository: local repo initialized on branch `main` for public GitHub publishing.
- Mod icon: `src/main/resources/placeable_gases_mod_icon.png`, referenced by `logoFile` in `META-INF/mods.toml`.
- Gas canister item icon: `src/main/resources/assets/placeablegases/textures/item/gas_canister.png`, referenced by `assets/placeablegases/models/item/gas_canister.json`.
- Keep generated/runtime directories such as `.gradle`, `build`, `run`, and `run-data` out of source renames unless explicitly needed.
- Prefer existing Forge MDK/Gradle conventions and keep changes scoped.

## Java Package Layout

- `com.ie04.placeablegases`: mod entrypoint and top-level orchestration.
- `com.ie04.placeablegases.api`: public extension points for addon mods and external integrations.
- `com.ie04.placeablegases.gas`: gas definitions, state, properties, and runtime gas data models.
- `com.ie04.placeablegases.registry`: deferred registers and central registration helpers.
- `com.ie04.placeablegases.block`: blocks for gas storage, containment, machinery, and world interaction.
- `com.ie04.placeablegases.item`: items for gas containers, tools, upgrades, and player interaction.
- `com.ie04.placeablegases.fluid`: Forge fluid interoperability and gas-fluid conversion support.
- `com.ie04.placeablegases.world`: world storage, chunk attachments, saved data, and environmental queries.
- `com.ie04.placeablegases.simulation`: gas cloud simulation, diffusion, movement, and tick scheduling.
- `com.ie04.placeablegases.reaction`: chemical reaction rules, hazards, combustion, and environmental effects.
- `com.ie04.placeablegases.pressure`: atmospheric pressure, compression, depressurization, and vacuum mechanics.
- `com.ie04.placeablegases.config`: Forge config specs and loaded configuration values.
- `com.ie04.placeablegases.network`: network messages and synchronization for client-visible gas state.
- `com.ie04.placeablegases.util`: shared utility helpers such as gas NBT serialization.

## Current Foundation

- Gas model skeleton exists: `Gas`, `GasProperties`, `GasBehavior`, `GasStack`, `GasRegistry`, and placeholder behavior contexts.
- Public gas API exists in `GasApi` as a wrapper around the custom gas registry.
- Built-in gas definitions are registered through `ModGases` for hydrogen, oxygen, and chlorine.
- `ModFluids` contains inert placeholder Forge `Fluid` registrations for storage compatibility; replace these with real Forge fluid implementations when tank/storage work begins.
- Gas canisters exist as the first pressure-aware storage item. They serialize `GasStack` data to ItemStack NBT through `GasNbt` and currently release gas only as debug chat output.
- Gas canisters have a fixed volume capacity and max pressure rating. Use `GasCanisterItem.pressurize(...)` for future compressors/fill sources so inserted gas stops at the pressure limit.
- `GasStack.amount` is treated as mB of standard gas volume. Hydrogen, oxygen, and chlorine use the default `GasProperties.pressureMultiplier` of `1.0`, so they produce identical pressure per mB in a canister. Future exotic/modded gases can use higher multipliers to hit pressure limits at lower mB.
- Do not implement world gas simulation, real pressure mechanics, reactions, rendering, tanks, machines, networking, or GUIs until the foundational API boundaries are stable.

## Project Prompt

Build Placeable Gases as a highly modular atmospheric simulation mod for Minecraft. The mod should make realistic gas behavior, pressure systems, and chemical interactions feel like first-class world mechanics rather than a reskinned fluid system.

Gases should be storable inside pressurized tanks, pipes, and portable canisters, and releasable into the environment as dynamically expanding voxel clouds. These clouds should respond to pressure, temperature, altitude, containment, air exposure, and surrounding chemistry. Dense gases should sink into caves, trenches, and low spaces, while lighter gases should rise, disperse, or escape toward open air.

The design direction should support hazardous and reactive chemistry. Volatile compounds may ignite, explode, corrode, poison, displace breathable air, or react violently when exposed to incompatible gases, liquids, blocks, temperature ranges, or atmospheric conditions.

Core systems to design toward:

- Dynamic voxel-based gas cloud simulation.
- Ambient atmospheric pressure system.
- Reduced pressure at high altitude.
- Increased pressure in underground or enclosed environments.
- Pressurized storage tanks and portable gas canisters.
- Gas diffusion, compression, and depressurization mechanics.
- Realistic density behavior for heavy and light gases.
- Hazardous gas chemistry, including toxicity and corrosiveness.
- Combustion, ignition, explosion, and runaway reaction systems.
- Vacuum-sealed environments and containment mechanics.
- Easy and Advanced gameplay modes.
- Full interoperability with Forge fluid systems.
- Extensive API support for addon developers and other mods.

Easy Mode should make gases manageable with simple interactions. Players should be able to capture released gases directly back into compatible containers, keeping atmospheric management accessible and lightweight.

Advanced Mode should focus on industrial-scale atmospheric engineering with compressors, pumps, pressure regulators, vacuum chambers, gas separators, containment systems, and controlled depressurization mechanisms. Poor handling of volatile gases should be able to cause explosions, toxic leaks, runaway reactions, or containment failures.

Treat Placeable Gases as a platform for atmospheric and chemical simulation. The modding API should let other mod authors register custom gases, define reaction chains, create storage systems, add specialized industrial machinery, implement atmospheric effects, and integrate existing Forge fluids into the gas system.

Each gas definition should be able to express properties such as density, diffusion rate, flammability, toxicity, corrosiveness, pressure tolerance, thermal behavior, reaction logic, and environmental interactions.

The long-term vision is to support realistic industrial chemistry, hazardous planetary atmospheres, advanced engineering gameplay, and large-scale environmental simulation through a coherent gas simulation framework.

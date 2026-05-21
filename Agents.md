# Agents.md

Project notes for coding agents working on Placeable Gases Mod.

- Project: Forge 1.20.1 mod named Placeable Gases Mod.
- Mod id: `placeablegases`.
- Author: Iyad Eltifi (`ie04`).
- Base Java package: `com.placeablegases`.
- Main mod class: `PlaceableGasesMod`.
- Git repository: local repo initialized on branch `main` for public GitHub publishing.
- Keep generated/runtime directories such as `.gradle`, `build`, `run`, and `run-data` out of source renames unless explicitly needed.
- Prefer existing Forge MDK/Gradle conventions and keep changes scoped.

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

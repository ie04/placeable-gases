# Placeable Gases

Placeable Gases is a Forge mod for Minecraft 1.20.1 focused on atmospheric simulation, pressurized gas mechanics, and reactive industrial chemistry.

The goal is to make gases behave like meaningful world systems instead of reskinned fluids. Gases should be stored, transported, compressed, released, contained, reacted, and engineered around.

> Development status: early project scaffold. Core simulation systems are not implemented yet.

## Vision

Placeable Gases aims to simulate gases as dynamic voxel clouds that exist in the world and respond to their surroundings. A released gas should spread, rise, sink, compress, disperse, ignite, poison, corrode, or react based on its own properties and the environment around it.

The long-term goal is to support:

- Realistic industrial gas handling.
- Hazardous chemical engineering gameplay.
- Environmental and atmospheric simulation.
- Vacuum and containment mechanics.
- Forge fluid interoperability.
- A public API for addon mods.

## Planned Features

### Dynamic Gas Clouds

Gases are planned to spread through the world as voxel-based atmospheric clouds rather than static blocks or ordinary fluids.

Planned behavior includes:

- Diffusion through open air.
- Compression in enclosed spaces.
- Depressurization through openings.
- Density-based movement.
- Heavy gases settling into low spaces.
- Light gases rising and escaping upward.
- Temperature, altitude, and pressure influence.
- Interaction with blocks, air exposure, and nearby chemicals.

### Atmospheric Pressure

The mod is planned to include an ambient pressure system that affects gas behavior.

Design targets:

- Lower pressure at high altitude.
- Higher pressure underground or in enclosed spaces.
- Pressure gradients that influence gas flow.
- Vacuum-sealed areas.
- Containment failure when pressure exceeds safe limits.

### Storage And Transport

Gases should be usable in industrial systems, not only released into the world.

Planned storage and transport systems include:

- Pressurized tanks.
- Portable gas canisters.
- Pipes.
- Pumps.
- Compressors.
- Pressure regulators.
- Vacuum chambers.
- Gas separators.
- Controlled vents and release valves.

### Chemical Reactions

Gas definitions are planned to support hazardous and reactive behavior.

Possible gas properties:

- Density.
- Diffusion rate.
- Flammability.
- Toxicity.
- Corrosiveness.
- Pressure tolerance.
- Thermal behavior.
- Reaction logic.
- Environmental interactions.

Possible reactions:

- Ignition.
- Combustion.
- Explosions.
- Corrosion.
- Poisoning.
- Oxygen displacement.
- Runaway reactions.
- Reactions with incompatible gases, fluids, blocks, or temperatures.

### Gameplay Modes

Placeable Gases is planned to support multiple levels of complexity.

Easy Mode should keep gas handling accessible. Players should be able to capture gases directly back into compatible containers with simple interactions.

Advanced Mode should focus on industrial-scale atmospheric engineering, including compressors, pumps, pressure regulators, vacuum chambers, separators, containment systems, and controlled depressurization.

## Modding API Goals

Placeable Gases is intended to become a platform for atmospheric and chemical simulation.

The planned API should allow addon developers to:

- Register custom gases.
- Define reaction chains.
- Create custom gas storage systems.
- Add specialized industrial machinery.
- Implement unique atmospheric effects.
- Integrate existing Forge fluids into the gas system.

## Current Technical Details

- Minecraft: `1.20.1`
- Forge: `47.4.20`
- Mod id: `placeablegases`
- Main package: `com.placeablegases`
- Main mod class: `PlaceableGasesMod`
- Author: Iyad Eltifi (`ie04`)
- Java target: `17`
- Build system: Gradle with ForgeGradle

## Building From Source

Clone the repository, then run:

```powershell
.\gradlew.bat build
```

On macOS or Linux:

```bash
./gradlew build
```

The built mod jar will be generated under:

```text
build/libs/
```

If Gradle dependency resolution fails, make sure your IDE or Gradle settings are not in offline mode. Forge dependencies are resolved from:

```text
https://maven.minecraftforge.net/
```

## Development Setup

For IntelliJ IDEA:

1. Import the project from `build.gradle`.
2. Let Gradle finish syncing dependencies.
3. Run:

```powershell
.\gradlew.bat genIntellijRuns
```

4. Refresh the Gradle project if run configurations do not appear automatically.

For Eclipse:

```powershell
.\gradlew.bat genEclipseRuns
```

## Repository Notes

Generated and runtime folders are intentionally ignored:

- `.gradle`
- `build`
- `run`
- `run-data`
- IDE metadata folders

Source files, Gradle wrapper files, Forge metadata, and documentation are intended to be committed.

## License

This project currently uses `All Rights Reserved` in the mod metadata. The repository also contains the Forge MDK license files inherited from the starter project.

Before public releases or accepting outside contributions, the project license should be reviewed and clarified.

## Roadmap

Near-term work should focus on turning the scaffold into a real foundation:

- Define the core gas data model.
- Create gas registry abstractions.
- Add starter gas types.
- Prototype gas cloud storage in world chunks.
- Implement basic diffusion and density behavior.
- Add debug commands or visualizers for simulation state.
- Design storage containers and pressure units.
- Establish API boundaries before adding complex machinery.

Longer-term work can build toward:

- Full pressure simulation.
- Cross-chunk gas movement.
- Multiplayer synchronization.
- Industrial machines.
- Forge fluid bridging.
- Gas reactions and hazards.
- Vacuum systems.
- Addon developer documentation.

package com.ie04.placeablegases.gas;

public record GasProperties(
        float density,
        float pressureMultiplier,
        float diffusivity,
        float toxicity,
        float flammability,
        float explosiveness,
        int color,
        int safePressure,
        int maxPressure,
        boolean reactsWithAir
) {
    public GasProperties(
            float density,
            float diffusivity,
            float toxicity,
            float flammability,
            float explosiveness,
            int color,
            int safePressure,
            int maxPressure,
            boolean reactsWithAir
    ) {
        this(density, 1.0f, diffusivity, toxicity, flammability, explosiveness, color, safePressure, maxPressure, reactsWithAir);
    }

    public GasProperties
    {
        if (pressureMultiplier <= 0.0f)
            throw new IllegalArgumentException("Gas pressure multiplier must be positive.");
    }
}

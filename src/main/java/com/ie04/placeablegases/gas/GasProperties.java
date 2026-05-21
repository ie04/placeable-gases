package com.ie04.placeablegases.gas;

public record GasProperties(
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
}

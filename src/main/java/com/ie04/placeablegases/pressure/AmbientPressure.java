package com.ie04.placeablegases.pressure;

public final class AmbientPressure
{
    public static final int SEA_LEVEL_Y = 64;
    public static final int STANDARD_AIR_UNITS = 1000;

    private AmbientPressure()
    {
    }

    public static int airUnitsAtY(int y)
    {
        int altitudeDelta = y - SEA_LEVEL_Y;
        float pressureFactor = 1.0f - altitudeDelta * 0.003f;
        return Math.max(250, Math.min(1800, Math.round(STANDARD_AIR_UNITS * pressureFactor)));
    }
}

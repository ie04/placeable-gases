package com.ie04.placeablegases.gas;

import java.util.Objects;

public class GasStack
{
    private final Gas gas;
    private int amount;
    private int pressure;
    private int temperature;
    private int purity;

    public GasStack(Gas gas, int amount, int pressure, int temperature, int purity)
    {
        this.gas = Objects.requireNonNull(gas, "gas");
        setAmount(amount);
        setPressure(pressure);
        this.temperature = temperature;
        setPurity(purity);
    }

    public Gas getGas()
    {
        return gas;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        if (amount < 0)
            throw new IllegalArgumentException("Gas amount cannot be negative.");

        this.amount = amount;
    }

    public int getPressure()
    {
        return pressure;
    }

    public void setPressure(int pressure)
    {
        if (pressure < 0)
            throw new IllegalArgumentException("Gas pressure cannot be negative.");

        this.pressure = pressure;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }

    public int getPurity()
    {
        return purity;
    }

    public void setPurity(int purity)
    {
        this.purity = Math.max(0, Math.min(100, purity));
    }

    public boolean isEmpty()
    {
        return amount == 0;
    }
}

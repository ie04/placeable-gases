package com.ie04.placeablegases.gas;

public record ReactionResult(boolean reacted, GasStack remainingStack) {
    public static ReactionResult none(GasStack remainingStack)
    {
        return new ReactionResult(false, remainingStack);
    }

    public static ReactionResult reacted(GasStack remainingStack)
    {
        return new ReactionResult(true, remainingStack);
    }
}

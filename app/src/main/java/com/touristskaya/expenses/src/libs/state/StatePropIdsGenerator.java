package com.touristskaya.expenses.src.libs.state;

/**
 * TODO: Add a class header comment
 */

public class StatePropIdsGenerator {
    private static int mNextId = 1;

    public static int nextId() {
        return ++mNextId;
    }
}

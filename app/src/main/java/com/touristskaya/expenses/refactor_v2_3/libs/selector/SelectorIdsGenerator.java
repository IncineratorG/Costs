package com.touristskaya.expenses.refactor_v2_3.libs.selector;

/**
 * TODO: Add a class header comment
 */

public class SelectorIdsGenerator {
    private static int mNextId = 1;

    public static int nextId() {
        return ++mNextId;
    }
}

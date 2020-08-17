package com.touristskaya.expenses.src.libs.state;

import com.touristskaya.expenses.src.libs.void_function.VoidFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add a class header comment
 */

public class State {
    private int mIdsCounter = 1;
    private Map<Integer, VoidFunction> mSelectors;

    public State() {
        mSelectors = new HashMap<>();
    }

    private int getNextSelectorId() {
        return ++mIdsCounter;
    }

    public VoidFunction select(VoidFunction selector) {
        if (selector == null) {
            return null;
        }

        selector.invoke();

        final int selectorId = getNextSelectorId();

        mSelectors.put(selectorId, selector);
        return () -> mSelectors.remove(selectorId);
    }

    public void update(VoidFunction updater) {
        if (updater == null) {
            return;
        }

        updater.invoke();

        Collection<VoidFunction> selectors = mSelectors.values();
        for (VoidFunction selector : selectors) {
            selector.invoke();
        }
    }
}

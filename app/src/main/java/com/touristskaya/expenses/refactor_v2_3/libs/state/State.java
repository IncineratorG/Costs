package com.touristskaya.expenses.refactor_v2_3.libs.state;

import com.touristskaya.expenses.refactor_v2_3.libs.selector.Selector;
import com.touristskaya.expenses.refactor_v2_3.libs.void_function.VoidFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add a class header comment
 */

public abstract class State {
    private int mIdsCounter = 1;
    private Map<Integer, Selector> mSelectors;

    public State() {
        mSelectors = new HashMap<>();
    }

    private int getNextSelectorId() {
        return ++mIdsCounter;
    }

    public VoidFunction select(Selector selector) {
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

        Collection<Selector> selectors = mSelectors.values();
        for (Selector selector : selectors) {
            selector.invoke();
        }
    }
}

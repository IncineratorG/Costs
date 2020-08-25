package com.touristskaya.expenses.src.libs.selector;

import androidx.core.util.Consumer;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add a class header comment
 */

public class Selector {
    private Consumer<Selector> mSelectorFunction;
    private Map<String, Object> mPrevValues;

    public Selector(Consumer<Selector> selectorFunction) {
        mSelectorFunction = selectorFunction;
        mPrevValues = new HashMap<>();
    }

    public Object getPrevValue(String key) {
        return mPrevValues.get(key);
    }

    public void setPrevValue(String key, Object value) {
        mPrevValues.put(key, value);
    }

    public void invoke() {
        if (mSelectorFunction != null) {
            mSelectorFunction.accept(this);
        }
    }
}

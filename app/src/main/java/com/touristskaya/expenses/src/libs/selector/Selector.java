package com.touristskaya.expenses.src.libs.selector;

import androidx.core.util.Consumer;

/**
 * TODO: Add a class header comment
 */

public class Selector {
    private Consumer<Selector> mSelectorFunction;
    private Object mPrevValue;

    public Selector(Consumer<Selector> selectorFunction) {
        mSelectorFunction = selectorFunction;
    }

    public Object getPrevValue() {
        return mPrevValue;
    }

    public void setPrevValue(Object prevValue) {
        mPrevValue = prevValue;
    }

    public void invoke() {
        if (mSelectorFunction != null) {
            mSelectorFunction.accept(this);
        }
    }
}

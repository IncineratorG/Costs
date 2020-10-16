package com.touristskaya.expenses.src.libs.state_prop;

/**
 * TODO: Add a class header comment
 */

public class StateProp<T> extends StatePropLike {
    private T mVal;

    public StateProp() {

    }

    public StateProp(T val) {
        mVal = val;
    }

    public void set(T val) {
        mVal = val;
        setUpdated(true);
    }

    public T value() {
        return mVal;
    }
}

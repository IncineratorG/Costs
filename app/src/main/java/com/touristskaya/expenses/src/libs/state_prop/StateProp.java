package com.touristskaya.expenses.src.libs.state_prop;

import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import com.touristskaya.expenses.src.libs.void_function.VoidFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class StateProp<T> extends StatePropLike {
    private T mVal;
    private List<Pair<Integer, Consumer<T>>> mHandlers;
    private int mHandlerIdsCounter;

    public StateProp() {
        mHandlers = new ArrayList<>();
        mHandlerIdsCounter = 1;
    }

    public StateProp(T val) {
        mVal = val;
        mHandlers = new ArrayList<>();
        mHandlerIdsCounter = 1;
    }

    public void set(T val) {
        mVal = val;
        setUpdated(true);
        notifyHandlers(val);
    }

    public T value() {
        return mVal;
    }

    public VoidFunction onChange(Consumer<T> handler) {
        handler.accept(mVal);

        int handlerId = ++mHandlerIdsCounter;
        mHandlers.add(new Pair<>(handlerId, handler));

        return () -> {
            for (int i = 0; i < mHandlers.size(); ++i) {
                Pair<Integer, Consumer<T>> storedHandler = mHandlers.get(i);
                if (storedHandler.first != null && storedHandler.first == handlerId) {
                    mHandlers.remove(i);
                    break;
                }
            }
        };
    }

    private void notifyHandlers(T val) {
        for (Pair<Integer, Consumer<T>> handler : mHandlers) {
            if (handler.second != null) {
                handler.second.accept(val);
            }
        }
    }
}

package com.touristskaya.expenses.src.libs.store;

import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.reducer.Reducer;
import com.touristskaya.expenses.src.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public class Store {
    private State mState;
    private Reducer mReducer;
    private Dispatcher mDispatcher;

    public Store(State state, Reducer reducer) {
        mState = state;
        mReducer = reducer;
        mDispatcher = new Dispatcher(mState, mReducer);
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }

    public State getState() {
        return mState;
    }
}

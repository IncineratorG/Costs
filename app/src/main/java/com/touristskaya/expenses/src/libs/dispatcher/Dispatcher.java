package com.touristskaya.expenses.src.libs.dispatcher;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.reducer.Reducer;
import com.touristskaya.expenses.src.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public class Dispatcher {
    private State mState;
    private Reducer mReducer;

    public Dispatcher(State state, Reducer reducer) {
        mState = state;
        mReducer = reducer;
    }

    public void dispatch(Action action) {
        mReducer.reduce(mState, action);
    }
}

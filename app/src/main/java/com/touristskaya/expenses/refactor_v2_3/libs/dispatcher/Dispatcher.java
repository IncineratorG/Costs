package com.touristskaya.expenses.refactor_v2_3.libs.dispatcher;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.Reducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.State;

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

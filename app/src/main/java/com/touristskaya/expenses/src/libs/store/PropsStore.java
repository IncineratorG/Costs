package com.touristskaya.expenses.src.libs.store;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.reducer.PropsReducer;
import com.touristskaya.expenses.src.libs.state.PropsState;

/**
 * TODO: Add a class header comment
 */

public class PropsStore {
    private PropsState mState;
    private PropsReducer mReducer;

    public PropsStore(PropsState state, PropsReducer reducer) {
        mState = state;
        mReducer = reducer;
    }

    public void dispatch(Action action) {
        mReducer.reduce(mState, action);
    }
}

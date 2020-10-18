package com.touristskaya.expenses.refactor_v2_3.libs.store;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.PropsReducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.PropsState;

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

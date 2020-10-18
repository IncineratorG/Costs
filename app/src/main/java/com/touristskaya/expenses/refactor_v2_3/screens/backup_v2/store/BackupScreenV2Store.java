package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.middleware.Middleware;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.PropsReducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.PropsState;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Store {
    private PropsState mState;
    private PropsReducer mReducer;
    private Middleware mMiddleware;

    public BackupScreenV2Store(PropsState state, PropsReducer reducer) {
        mState = state;
        mReducer = reducer;
    }

    public void applyMiddleware(Middleware middleware) {
        mMiddleware = middleware;
    }

    public void dispatch(Action action) {
        if (mMiddleware != null) {
            mMiddleware.onAction(action);
        }
        mReducer.reduce(mState, action);
    }
}

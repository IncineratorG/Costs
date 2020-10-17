package com.touristskaya.expenses.src.screens.backup_v2.store;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.libs.reducer.PropsReducer;
import com.touristskaya.expenses.src.libs.state.PropsState;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Store {
    private PropsState mState;
    private PropsReducer mReducer;
    private Middleware mMiddleware;

    public BackupScreenV2Store(PropsState state, PropsReducer reducer, Middleware middleware) {
        mState = state;
        mReducer = reducer;
        mMiddleware = middleware;
    }

    public void dispatch(Action action) {
        mMiddleware.onAction(action);
        mReducer.reduce(mState, action);
    }
}

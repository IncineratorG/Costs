package com.touristskaya.expenses.src.libs.reducer;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.state.PropsState;

/**
 * TODO: Add a class header comment
 */

public interface PropsReducer {
    void reduce(PropsState state, Action action);
}

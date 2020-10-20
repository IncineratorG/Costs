package com.touristskaya.expenses.refactor_v2_3.libs.reducer;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public interface Reducer {
    void reduce(State state, Action action);
}

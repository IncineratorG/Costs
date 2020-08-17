package com.touristskaya.expenses.src.libs.reducer;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public interface Reducer {
    void reduce(State state, Action action);
}

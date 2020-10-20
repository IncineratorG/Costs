package com.touristskaya.expenses.refactor_v2_3.libs.middleware;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;

/**
 * TODO: Add a class header comment
 */

public interface Middleware {
    void onAction(Action action);
}

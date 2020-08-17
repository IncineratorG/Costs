package com.touristskaya.expenses.src.libs.middleware;

import com.touristskaya.expenses.src.libs.action.Action;

/**
 * TODO: Add a class header comment
 */

public interface Middleware {
    void onAction(Action action);
}

package com.touristskaya.expenses.refactor_v2_3.old_stores.middleware.system;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.middleware.Middleware;
import com.touristskaya.expenses.refactor_v2_3.old_stores.middleware.system.handlers.SystemMiddlewareHandlers;
import com.touristskaya.expenses.refactor_v2_3.old_stores.types.system.SystemActionTypes;

/**
 * TODO: Add a class header comment
 */

public class SystemMiddleware implements Middleware {
    private SystemMiddlewareHandlers mSystemHandlers = new SystemMiddlewareHandlers();

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case (SystemActionTypes.UPDATE_NETWORK_CONNECTION): {
                mSystemHandlers.updateNetworkConnectionHandler(action);
                break;
            }
        }
    }
}

package com.touristskaya.expenses.src.stores.middleware.system;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.stores.middleware.system.handlers.SystemMiddlewareHandlers;
import com.touristskaya.expenses.src.stores.types.system.SystemActionTypes;

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

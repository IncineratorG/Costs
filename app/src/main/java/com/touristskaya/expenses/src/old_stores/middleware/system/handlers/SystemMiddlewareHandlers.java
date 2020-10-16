package com.touristskaya.expenses.src.old_stores.middleware.system.handlers;

import android.content.Context;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.services.system.SystemService;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

/**
 * TODO: Add a class header comment
 */

public class SystemMiddlewareHandlers {
    public void updateNetworkConnectionHandler(Action action) {
        Context context = (Context) action.getPayload();
        if (context == null) {
            SystemEventsHandler.onError("updateNetworkConnectionHandler(): BAD_CONTEXT");
            return;
        }

        SystemService systemService = (SystemService) AppServices.getInstance().get(AppServices.SYSTEM_SERVICE);
        systemService.updateNetworkConnectionInfo(context);
    }
}

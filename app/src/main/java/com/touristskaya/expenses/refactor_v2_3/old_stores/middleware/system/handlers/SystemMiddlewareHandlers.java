package com.touristskaya.expenses.refactor_v2_3.old_stores.middleware.system.handlers;

import android.content.Context;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.services.AppServices;
import com.touristskaya.expenses.refactor_v2_3.services.system.SystemService;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;

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

        SystemService systemService = (SystemService) AppServices.get().getService(AppServices.SYSTEM_SERVICE);
        systemService.updateNetworkConnectionInfo(context);
    }
}

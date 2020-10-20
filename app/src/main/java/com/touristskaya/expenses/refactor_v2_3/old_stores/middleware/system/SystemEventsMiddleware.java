package com.touristskaya.expenses.refactor_v2_3.old_stores.middleware.system;

import androidx.core.util.Consumer;

import com.touristskaya.expenses.refactor_v2_3.libs.middleware.EventsMiddleware;
import com.touristskaya.expenses.refactor_v2_3.libs.void_function.VoidFunction;
import com.touristskaya.expenses.refactor_v2_3.services.AppServices;
import com.touristskaya.expenses.refactor_v2_3.services.system.SystemService;
import com.touristskaya.expenses.refactor_v2_3.services.system.data.event_types.SystemServiceEvents;
import com.touristskaya.expenses.refactor_v2_3.old_stores.OldAppStore;
import com.touristskaya.expenses.refactor_v2_3.old_stores.actions.system.SystemActions;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class SystemEventsMiddleware implements EventsMiddleware {
    private List<VoidFunction> mUnsubscribeFunctions;

    public SystemEventsMiddleware() {
        mUnsubscribeFunctions = new ArrayList<>();
    }

    @Override
    public void init() {
        for (int i = 0; i < mUnsubscribeFunctions.size(); ++i) {
            mUnsubscribeFunctions.get(i).invoke();
        }
        mUnsubscribeFunctions.clear();

        SystemService systemService = (SystemService) AppServices.
                get()
                .getService(AppServices.SYSTEM_SERVICE);

        Consumer<Object> testHandler = (testTextObject) -> {
            String str = (String) testTextObject;
            SystemEventsHandler.onInfo("FROM_MIDDLEWARE: " + str);
        };
        Consumer<Object> hasNetworkConnectionHandler = (hasNetworkConnectionObject) -> {
          boolean hasNetworkConnection = (boolean) hasNetworkConnectionObject;
          OldAppStore.dispatch(SystemActions.setHasNetworkConnectionAction(hasNetworkConnection));
        };

        VoidFunction testHandlerUnsubscribe = systemService.subscribe(SystemServiceEvents.TEST_EVENT, testHandler);
        VoidFunction hasNetworkConnectionHandlerUnsubscribe = systemService.
                subscribe(SystemServiceEvents.NETWORK_CONNECTION_CHANGED, hasNetworkConnectionHandler);

        mUnsubscribeFunctions.add(testHandlerUnsubscribe);
        mUnsubscribeFunctions.add(hasNetworkConnectionHandlerUnsubscribe);
    }
}

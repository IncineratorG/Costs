package com.touristskaya.expenses.src.stores.middleware.system;

import androidx.core.util.Consumer;

import com.touristskaya.expenses.src.libs.middleware.EventsMiddleware;
import com.touristskaya.expenses.src.libs.void_function.VoidFunction;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.services.system.SystemService;
import com.touristskaya.expenses.src.services.system.data.event_types.SystemServiceEvents;
import com.touristskaya.expenses.src.stores.AppStore;
import com.touristskaya.expenses.src.stores.actions.system.SystemActions;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

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
                getInstance()
                .get(AppServices.SYSTEM_SERVICE);

        Consumer<Object> testHandler = (testTextObject) -> {
            String str = (String) testTextObject;
            SystemEventsHandler.onInfo("FROM_MIDDLEWARE: " + str);
        };
        Consumer<Object> hasNetworkConnectionHandler = (hasNetworkConnectionObject) -> {
          boolean hasNetworkConnection = (boolean) hasNetworkConnectionObject;
          AppStore.dispatch(SystemActions.setHasNetworkConnectionAction(hasNetworkConnection));
        };

        VoidFunction testHandlerUnsubscribe = systemService.subscribe(SystemServiceEvents.TEST_EVENT, testHandler);
        VoidFunction hasNetworkConnectionHandlerUnsubscribe = systemService.
                subscribe(SystemServiceEvents.NETWORK_CONNECTION_CHANGED, hasNetworkConnectionHandler);

        mUnsubscribeFunctions.add(testHandlerUnsubscribe);
        mUnsubscribeFunctions.add(hasNetworkConnectionHandlerUnsubscribe);
    }
}

package com.touristskaya.expenses.src.stores.middleware;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.EventsMiddleware;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.stores.middleware.system.SystemEventsMiddleware;
import com.touristskaya.expenses.src.stores.middleware.system.SystemMiddleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class RootMiddleware implements Middleware {
//    private Middleware mSecondMiddleware = new SecondMiddleware();
//
//    private List<Middleware> mMiddlewareList = new ArrayList<>(
//            Arrays.asList(
//                    mSecondMiddleware
//            )
//    );
//
//    @Override
//    public void onAction(Action action) {
//        for (Middleware m : mMiddlewareList) {
//            m.onAction(action);
//        }
//    }

    private Middleware mSystemMiddleware = new SystemMiddleware();

    private List<Middleware> mMiddlewareList = new ArrayList<>(
            Arrays.asList(
                    mSystemMiddleware
            )
    );

    private EventsMiddleware mSystemEventsMiddleware = new SystemEventsMiddleware();

    private List<EventsMiddleware> mEventsMiddlewareList = new ArrayList<>(
            Arrays.asList(
                    mSystemEventsMiddleware
            )
    );

    public RootMiddleware() {
        for (EventsMiddleware eventsMiddleware : mEventsMiddlewareList) {
            eventsMiddleware.init();
        }
    }

    @Override
    public void onAction(Action action) {
        for (Middleware m : mMiddlewareList) {
            m.onAction(action);
        }
    }
}

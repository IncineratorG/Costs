package com.touristskaya.expenses.src.stores.middleware;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.EventsMiddleware;
import com.touristskaya.expenses.src.libs.middleware.Middleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class RootMiddleware implements Middleware {
    private List<Middleware> mMiddlewareList = new ArrayList<>(
            Arrays.asList(

            )
    );

    private List<EventsMiddleware> mEventsMiddlewareList = new ArrayList<>(
            Arrays.asList(

            )
    );

    public RootMiddleware() {
        for (EventsMiddleware eventsMiddleware : mEventsMiddlewareList) {
            eventsMiddleware.init();
        }
    }

    @Override
    public void onAction(Action action) {
        for (Middleware middleware : mMiddlewareList) {
            middleware.onAction(action);
        }
    }
}

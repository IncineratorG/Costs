package com.touristskaya.expenses.src.stores.middleware;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.EventsMiddleware;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.stores.middleware.backup.BackupMiddleware;
import com.touristskaya.expenses.src.stores.middleware.system.SystemEventsMiddleware;
import com.touristskaya.expenses.src.stores.middleware.system.SystemMiddleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RootMiddleware implements Middleware {
    private Middleware mSystemMiddleware = new SystemMiddleware();
    private Middleware mBackupMiddleware = new BackupMiddleware();

    private List<Middleware> mMiddlewareList = new ArrayList<>(
            Arrays.asList(
                    mSystemMiddleware,
                    mBackupMiddleware
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

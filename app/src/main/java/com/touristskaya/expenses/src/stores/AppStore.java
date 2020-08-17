package com.touristskaya.expenses.src.stores;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.libs.store.Store;
import com.touristskaya.expenses.src.stores.middleware.RootMiddleware;
import com.touristskaya.expenses.src.stores.reducers.system.SystemReducer;
import com.touristskaya.expenses.src.stores.states.system.SystemState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AppStore {
    private static Middleware mMiddleware = new RootMiddleware();

    public static SystemState systemState = new SystemState();

    private static Dispatcher mSystemDispatcher = new Store(systemState, new SystemReducer()).getDispatcher();

    private static List<Dispatcher> mDispatchers = new ArrayList<>(
            Arrays.asList(
                    mSystemDispatcher
            )
    );

    public static void init() {}

    public static void dispatch(Action action) {
        mMiddleware.onAction(action);

        for (Dispatcher d : mDispatchers) {
            d.dispatch(action);
        }
    }
}

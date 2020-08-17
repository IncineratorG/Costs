package com.touristskaya.expenses.src.stores;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.libs.store.Store;
import com.touristskaya.expenses.src.stores.middleware.SimpleMiddleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

class AppStore {
    private static Middleware mMiddleware = new SimpleMiddleware();

//    public static FirstState firstState = new FirstState();
//    public static SecondState secondState = new SecondState();

//    private static Dispatcher mFirstDispatcher = new Store(firstState, new FirstReducer()).getDispatcher();
//    private static Dispatcher mSecondDispatcher = new Store(secondState, new SecondReducer()).getDispatcher();

    private static List<Dispatcher> mDispatchers = new ArrayList<>(
            Arrays.asList()
    );

    public static void dispatch(Action action) {
        mMiddleware.onAction(action);

        for (Dispatcher d : mDispatchers) {
            d.dispatch(action);
        }
    }
}

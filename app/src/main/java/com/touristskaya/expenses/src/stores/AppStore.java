package com.touristskaya.expenses.src.stores;

import android.util.Log;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.libs.store.PropsStore;
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
    public static SystemState systemState = new SystemState();

    private static PropsStore mSystemStore = new PropsStore(systemState, new SystemReducer());

    private static List<PropsStore> mStores = new ArrayList<>(
            Arrays.asList(
                    mSystemStore
            )
    );

    private static Middleware mMiddleware = new RootMiddleware();

    public static void dispatch(Action action) {
        mMiddleware.onAction(action);

        for (PropsStore store : mStores) {
            store.dispatch(action);
        }
    }
}

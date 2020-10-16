package com.touristskaya.expenses.src.old_stores;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.libs.store.Store;
import com.touristskaya.expenses.src.old_stores.middleware.RootMiddleware;
import com.touristskaya.expenses.src.old_stores.reducers.backup.BackupReducer;
import com.touristskaya.expenses.src.old_stores.reducers.system.SystemReducer;
import com.touristskaya.expenses.src.old_stores.states.backup.BackupState;
import com.touristskaya.expenses.src.old_stores.states.system.SystemState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class OldAppStore {
    private static Middleware mMiddleware = new RootMiddleware();

    public static SystemState systemState = new SystemState();
    public static BackupState backupState = new BackupState();

    private static Dispatcher mSystemDispatcher = new Store(systemState, new SystemReducer()).getDispatcher();
    private static Dispatcher mBackupDispatcher = new Store(backupState, new BackupReducer()).getDispatcher();

    private static List<Dispatcher> mDispatchers = new ArrayList<>(
            Arrays.asList(
                    mSystemDispatcher,
                    mBackupDispatcher
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

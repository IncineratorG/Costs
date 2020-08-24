package com.touristskaya.expenses.src.screens.backup.models;

import android.content.Context;

import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.selector.Selector;
import com.touristskaya.expenses.src.libs.state.State;
import com.touristskaya.expenses.src.libs.store.Store;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenReducer;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenState;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.stores.AppStore;
import com.touristskaya.expenses.src.stores.actions.system.SystemActions;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenModel {
    private Context mContext;
    private BackupScreenState mState;
    private BackupScreenReducer mReducer;
    private Dispatcher mDispatcher;

    public BackupScreenModel(Context context) {
        mContext = context;

        mState = new BackupScreenState();
        mReducer = new BackupScreenReducer();
        mDispatcher = new Store(mState, mReducer).getDispatcher();

        AppStore.init();
        AppServices.getInstance().init();

        AppStore.systemState.select(new Selector((selector) -> {
            Boolean prevHasNetworkConnection = (Boolean) selector.getPrevValue();
            boolean currHasNetworkConnection = AppStore.systemState.hasNetworkConnection;

            if (prevHasNetworkConnection == null || prevHasNetworkConnection != currHasNetworkConnection) {
                mState.update(() -> {
                    mState.hasNetworkConnection = currHasNetworkConnection;
                });
            }

            selector.setPrevValue(currHasNetworkConnection);
        }));

        AppStore.dispatch(SystemActions.updateNetworkConnectionAction(mContext));
    }

    public State getState() {
        return mState;
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }
}

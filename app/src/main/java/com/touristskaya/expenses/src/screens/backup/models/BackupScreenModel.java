package com.touristskaya.expenses.src.screens.backup.models;

import android.content.Context;

import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.stores.AppStore;
import com.touristskaya.expenses.src.stores.actions.system.SystemActions;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenModel {
    private Context mContext;

    public BackupScreenModel(Context context) {
        mContext = context;

        AppStore.init();
        AppServices.getInstance().init();

        AppStore.systemState.select(() -> {
            SystemEventsHandler.onInfo("HAS_CONNECTION: " + AppStore.systemState.hasNetworkConnection);
        });

        AppStore.dispatch(SystemActions.updateNetworkConnectionAction(mContext));
    }
}

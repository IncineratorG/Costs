package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.models;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.core.util.Consumer;

import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.selector.PropsSelector;
import com.touristskaya.expenses.refactor_v2_3.libs.state_prop.StateProp;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.BackupScreenV2Store;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.middleware.BackupScreenV2Middleware;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.reducer.BackupScreenV2Reducer;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.state.BackupScreenV2State;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;

import java.util.Arrays;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Model {
    public Activity currentActivity;
    public String appName = "";
    public StateProp<Boolean> hasNetworkConnection = new StateProp<>(false);

    public StateProp<Boolean> signedIn = new StateProp<>(false);
    public StateProp<Boolean> signingIn = new StateProp<>(false);

    public Consumer<Action> dispatch;
    public BackupScreenV2State state;

    private BackupScreenV2Store mStore;

    public BackupScreenV2Model(Activity activity) {
        currentActivity = activity;
        appName = getAppName();

        state = new BackupScreenV2State();
        BackupScreenV2Reducer reducer = new BackupScreenV2Reducer();
        mStore = new BackupScreenV2Store(state, reducer);
        BackupScreenV2Middleware middleware = new BackupScreenV2Middleware(mStore, state);
        mStore.applyMiddleware(middleware);

        dispatch = (action) -> mStore.dispatch(action);

        state.select(new PropsSelector(
                Arrays.asList(state.acquiringDriveService, state.googleDriveService),
                () -> {
//                    SystemEventsHandler.onInfo("HERE_CHANGED");

                    boolean acquiringDriveService = state.acquiringDriveService.value();
                    Drive driveService = state.googleDriveService.value();

                    if (acquiringDriveService) {
                        signingIn.set(true);
                    } else if (driveService != null) {
                        signedIn.set(true);
                        signingIn.set(false);
                    } else {
                        signedIn.set(false);
                        signingIn.set(false);
                    }
                }
        ));

//        googleDriveService.onChange(drive -> {
//            SystemEventsHandler.onInfo("DRIVE: " + (drive == null));
//        });

//        mSystemState.select(new PropsSelector(
//                Collections.singletonList(mSystemState.hasNetworkConnection),
//                () -> hasNetworkConnection.set(mSystemState.hasNetworkConnection.value())
//        ));
    }

    private String getAppName() {
        PackageManager packageManager = currentActivity.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(currentActivity.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            SystemEventsHandler.onError("getAppLabel->NameNotFoundException");
        }

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }
}

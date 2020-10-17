package com.touristskaya.expenses.src.screens.backup_v2.models;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.core.util.Consumer;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.state_prop.StateProp;
import com.touristskaya.expenses.src.libs.store.PropsStore;
import com.touristskaya.expenses.src.screens.backup_v2.store.BackupScreenV2Store;
import com.touristskaya.expenses.src.screens.backup_v2.store.middleware.BackupScreenV2Middleware;
import com.touristskaya.expenses.src.screens.backup_v2.store.reducer.BackupScreenV2Reducer;
import com.touristskaya.expenses.src.screens.backup_v2.store.state.BackupScreenV2State;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Model {
    public Activity currentActivity;
    public String appName = "";
    public StateProp<Boolean> hasNetworkConnection = new StateProp<>(false);

    public final int SIGN_IN_REQUEST_CODE = 1;
    public StateProp<Drive> googleDriveService = new StateProp<>(null);
    public StateProp<GoogleSignInClient> googleSignInClient = new StateProp<>(null);

    public Consumer<Action> dispatch;
    public BackupScreenV2State mState;

    private BackupScreenV2Store mStore;

    public BackupScreenV2Model(Activity activity) {
        currentActivity = activity;
        appName = getAppName();

        mState = new BackupScreenV2State();
        BackupScreenV2Reducer reducer = new BackupScreenV2Reducer();
        BackupScreenV2Middleware middleware = new BackupScreenV2Middleware();
        mStore = new BackupScreenV2Store(mState, reducer, middleware);

        dispatch = (action) -> mStore.dispatch(action);

        googleDriveService.onChange(drive -> {
            SystemEventsHandler.onInfo("DRIVE: " + (drive == null));
        });

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

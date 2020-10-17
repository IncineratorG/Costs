package com.touristskaya.expenses.src.screens.backup_v2.models;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.src.libs.selector.PropsSelector;
import com.touristskaya.expenses.src.libs.state.PropsState;
import com.touristskaya.expenses.src.libs.state_prop.StateProp;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.stores.AppStore;
import com.touristskaya.expenses.src.stores.states.system.SystemState;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

import java.util.Arrays;
import java.util.Collections;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Model {
    public Activity currentActivity;
    public String appName = "";
    public StateProp<Boolean> hasNetworkConnection = new StateProp<>(false);
    public StateProp<GoogleSignInClient> googleSignInClient = new StateProp<>(null);
    public final int SIGN_IN_REQUEST_CODE = 1;
    public StateProp<Drive> googleDriveService = new StateProp<>(null);

//    private SystemState mSystemState = AppStore.systemState;

    public BackupScreenV2Model(Activity activity) {
        currentActivity = activity;
        appName = getAppName();

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

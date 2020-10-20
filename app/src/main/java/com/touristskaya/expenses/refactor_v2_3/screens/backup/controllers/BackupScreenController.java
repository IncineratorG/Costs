package com.touristskaya.expenses.refactor_v2_3.screens.backup.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.touristskaya.expenses.ActivityMainWithFragments;
import com.touristskaya.expenses.refactor_v2_3.libs.payload.Payload;
import com.touristskaya.expenses.refactor_v2_3.libs.selector.PropsSelector;
import com.touristskaya.expenses.refactor_v2_3.screens.backup.models.BackupScreenModel;
import com.touristskaya.expenses.refactor_v2_3.screens.backup.store.BackupScreenState;
import com.touristskaya.expenses.refactor_v2_3.old_stores.OldAppStore;
import com.touristskaya.expenses.refactor_v2_3.old_stores.actions.backup.BackupActions;
import com.touristskaya.expenses.refactor_v2_3.stores.AppStore;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;

import java.util.Arrays;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenController {
    private BackupScreenModel mModel;
    private BackupScreenState mState;

    public BackupScreenController(BackupScreenModel model) {
        mModel = model;
        mState = (BackupScreenState) mModel.getState();

        AppStore.systemState.select(new PropsSelector(
                Arrays.asList(AppStore.systemState.hasNetworkConnection),
                () -> Log.d("tag", "ON_ACTION: " + AppStore.systemState.hasNetworkConnection.value())
        ));
    }

    public void createBackupButtonHandler() {
        Log.d("tag", "createBackupButtonHandler()");

//        SystemService systemService = (SystemService) AppServices.getInstance().get(AppServices.SYSTEM_SERVICE);
//        systemService.test();

//        SystemService systemService = (SystemService) AppServices.getInstance().get(AppServices.SYSTEM_SERVICE);
//        systemService.subscribe(SystemServiceEvents.TEST_EVENT, (result) -> {
//            String resultString = (String) result;
//            Log.d("tag", resultString);
//        });
    }

    public void signInButtonHandler() {
        SystemEventsHandler.onInfo("signInButtonHandler()");

//        AppStore.dispatch(
//                SystemActions.setHasNetworkConnectionAction(true)
//        );

//        requestSignIn();
    }

    public void requestSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(mState.currentActivity, signInOptions);

        mModel.getDispatcher().dispatch(BackupActions.setGoogleSignInClientAction(client));

        mState.currentActivity.startActivityForResult(client.getSignInIntent(), mState.signInRequestCode);
    }

    public void activityResultHandler(int requestCode, int resultCode, Intent resultData) {
        SystemEventsHandler.onInfo("activityResultHandler()");

        if (requestCode == mState.signInRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                handleSignInSuccessfulResult(resultData);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                SystemEventsHandler.onInfo("activityResultHandler()->CANCELLED");
            }
        } else {
            SystemEventsHandler.onError("activityResultHandler()->NOT_HERE");
        }
    }

    public void backButtonHandler() {
        Log.d("tag", "backButtonHandler()");

        Intent mainActivityWithFragmentsIntent = new Intent(mState.currentActivity, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mState.currentActivity.startActivity(mainActivityWithFragmentsIntent);
    }

    public void selectGoogleAccountIconHandler() {
        Log.d("tag", "selectGoogleAccountIconHandler()");

//        SystemService systemService = (SystemService) AppServices.getInstance().get(AppServices.SYSTEM_SERVICE);
//        systemService.test();
    }

    public void progressDialogCancelButtonHandler() {
        Log.d("tag", "progressDialogCancelButtonHandler()");
    }

    private void handleSignInSuccessfulResult(Intent result) {
        if (result == null) {
            SystemEventsHandler.onError("handleSignInResult()->DATA_IS_NULL");
            return;
        }

        OldAppStore.dispatch(BackupActions.setSignedInFlagAction(true));

        Payload payload = new Payload();
        payload.set("resultIntent", result);
        payload.set("context", mState.currentActivity);
        payload.set("appLabel", getAppLabel(mState.currentActivity));

        OldAppStore.dispatch(BackupActions.buildGoogleDriveServiceAction(payload));
    }

    private String getAppLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            SystemEventsHandler.onError("getAppLabel->NameNotFoundException");
        }

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }
}

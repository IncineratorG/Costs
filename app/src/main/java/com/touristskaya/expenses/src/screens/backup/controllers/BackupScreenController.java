package com.touristskaya.expenses.src.screens.backup.controllers;

import android.content.Intent;
import android.util.Log;

import com.touristskaya.expenses.ActivityMainWithFragments;
import com.touristskaya.expenses.src.screens.backup.BackupScreen;
import com.touristskaya.expenses.src.screens.backup.models.BackupScreenModel;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.services.system.SystemService;
import com.touristskaya.expenses.src.services.system.data.event_types.SystemServiceEvents;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenController {
    private BackupScreenModel mModel;

    public BackupScreenController(BackupScreenModel model) {
        mModel = model;
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
        Log.d("tag", "signInButtonHandler()");
    }

    public void backButtonHandler(BackupScreen activity) {
        Log.d("tag", "backButtonHandler()");

        Intent mainActivityWithFragmentsIntent = new Intent(activity, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(mainActivityWithFragmentsIntent);
    }

    public void selectGoogleAccountIconHandler() {
        Log.d("tag", "selectGoogleAccountIconHandler()");

//        SystemService systemService = (SystemService) AppServices.getInstance().get(AppServices.SYSTEM_SERVICE);
//        systemService.test();
    }

    public void progressDialogCancelButtonHandler() {
        Log.d("tag", "progressDialogCancelButtonHandler()");
    }
}

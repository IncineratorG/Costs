package com.touristskaya.expenses.src.screens.backup.controllers;

import android.content.Intent;
import android.util.Log;

import com.touristskaya.expenses.ActivityMainWithFragments;
import com.touristskaya.expenses.src.screens.backup.BackupScreen;
import com.touristskaya.expenses.src.screens.backup.models.BackupScreenModel;

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
    }

    public void progressDialogCancelButtonHandler() {
        Log.d("tag", "progressDialogCancelButtonHandler()");
    }
}

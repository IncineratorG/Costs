package com.touristskaya.expenses.src.screens.backup.controllers;

import android.util.Log;

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

    public void arrowBackIconHandler() {
        Log.d("tag", "arrowBackIconHandler()");
    }

    public void selectGoogleAccountIconHandler() {
        Log.d("tag", "selectGoogleAccountIconHandler()");
    }

    public void progressDialogCancelButtonHandler() {
        Log.d("tag", "progressDialogCancelButtonHandler()");
    }
}

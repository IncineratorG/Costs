package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.touristskaya.expenses.ActivityMainWithFragments;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.models.BackupScreenV2Model;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.actions.BackupScreenV2Actions;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;

public class BackupScreenV2Controller {
    private BackupScreenV2Model mModel;

    public BackupScreenV2Controller(BackupScreenV2Model model) {
        mModel = model;
    }

    public boolean hasNetworkConnectionHandler() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mModel.currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            mModel.hasNetworkConnection.set(networkInfo != null);
            return networkInfo != null;
        } else {
            mModel.hasNetworkConnection.set(false);
            return false;
        }
    }

    public void backButtonHandler() {
        Intent mainActivityWithFragmentsIntent = new Intent(mModel.currentActivity, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mModel.currentActivity.startActivity(mainActivityWithFragmentsIntent);
    }

    public void signInHandler() {
        mModel.dispatch.accept(BackupScreenV2Actions.getDriveServiceAction(mModel.currentActivity));
    }

    public void signOutHandler() {
        mModel.state.googleSignInClient.value().signOut().addOnCompleteListener((t) -> mModel.signedIn.set(false));
    }

    public void getBackupDataHandler() {
        SystemEventsHandler.onInfo("getBackupDataHandler()");

//        SystemEventsHandler.onInfo("1->" + mModel.state.gettingBackupData.propId());
//        SystemEventsHandler.onInfo("2-> " + mModel.state.googleSignInClient.propId());
//        SystemEventsHandler.onInfo("3->: " + mModel.state.googleDriveService.propId());
//        SystemEventsHandler.onInfo("4->: " + mModel.state.acquiringDriveService.propId());
//        SystemEventsHandler.onInfo("5->" + mModel.state.gettingBackupDataHasError.propId());
//        SystemEventsHandler.onInfo("6-> " + mModel.state.acquiringDriveServiceHasError.propId());
//        SystemEventsHandler.onInfo("7-> " + mModel.state.backupFilesList.propId());

//        mModel.dispatch.accept(BackupScreenV2Actions.getBackupDataBeginAction());
        mModel.dispatch.accept(BackupScreenV2Actions.getBackupDataAction(mModel.state.googleDriveService.value()));
    }

    public void activityResultHandler(int requestCode, int resultCode, Intent resultData) {
        mModel.dispatch.accept(
                BackupScreenV2Actions.activityResultAction(
                        requestCode,
                        resultCode,
                        resultData,
                        mModel.currentActivity,
                        mModel.appName
                )
        );
    }
}

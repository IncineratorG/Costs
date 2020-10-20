package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.actions;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.payload.Payload;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.action_types.BackupScreenV2ActionTypes;
import com.touristskaya.expenses.refactor_v1_active.activities.backup.DataUnitBackupFolder;

import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Actions {
    public static Action getDriveServiceAction(Activity currentActivity) {
        return new Action(BackupScreenV2ActionTypes.GET_DRIVE_SERVICE, currentActivity);
    }

    public static Action getDriveServiceBeginAction() {
        return new Action(BackupScreenV2ActionTypes.GET_DRIVE_SERVICE_BEGIN);
    }

    public static Action getDriveServiceFinishedAction(Drive googleDriveService) {
        return new Action(BackupScreenV2ActionTypes.GET_DRIVE_SERVICE_FINISHED, googleDriveService);
    }

    public static Action getDriveServiceErrorAction() {
        return new Action(BackupScreenV2ActionTypes.GET_DRIVE_SERVICE_ERROR);
    }

    public static Action setGoogleSignInClientAction(GoogleSignInClient client) {
        return new Action(BackupScreenV2ActionTypes.SET_GOOGLE_SIGN_IN_CLIENT, client);
    }

    public static Action activityResultAction(int requestCode,
                                              int resultCode,
                                              Intent resultData,
                                              Activity activity,
                                              String appName) {
        Payload payload = new Payload();
        payload.set("requestCode", requestCode);
        payload.set("resultCode", resultCode);
        payload.set("resultData", resultData);
        payload.set("activity", activity);
        payload.set("appName", appName);
        return new Action(BackupScreenV2ActionTypes.ACTIVITY_RESULT, payload);
    }

    public static Action getBackupDataAction(Drive driveService) {
        return new Action(BackupScreenV2ActionTypes.GET_BACKUP_DATA, driveService);
    }

    public static Action getBackupDataBeginAction() {
        return new Action(BackupScreenV2ActionTypes.GET_BACKUP_DATA_BEGIN);
    }

    public static Action getBackupDataFinishedAction(List<DataUnitBackupFolder> backupFilesList) {
        return new Action(BackupScreenV2ActionTypes.GET_BACKUP_DATA_FINISHED, backupFilesList);
    }

    public static Action getBackupDataErrorAction() {
        return new Action(BackupScreenV2ActionTypes.GET_BACKUP_DATA_ERROR);
    }
}

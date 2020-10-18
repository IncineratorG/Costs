package com.touristskaya.expenses.refactor_v1_active.services.realisation.backup;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.touristskaya.expenses.DB_Costs;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.callbacks.CreateDeviceBackupCompleted;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.callbacks.DeleteDeviceBackupCompleted;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.callbacks.GetBackupDataCompleted;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.callbacks.RestoreDataBaseFromBackupProgress;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.CreateDeviceBackupTask;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.DeleteDeviceBackupTask;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.GetBackupDataTask;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.RestoreDataBaseFromBackupTask;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.TaskRunner;

import java.util.Collections;

/**
 * TODO: Add a class header comment
 */
public class BackupService {
    private static final String TAG = "tag";

    private static final String CLASS_NAME = "BackupService";

    private TaskRunner mTaskRunner;


    public BackupService() {
        mTaskRunner = TaskRunner.getInstance();
    }

    public boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null;
    }

    public void getSignInAccount(Intent result, OnSuccessListener<GoogleSignInAccount> onSuccess, OnFailureListener onFailure) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public Drive getGoogleDriveService(GoogleSignInAccount signInAccount, Context context, String appName) {
        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
        credential.setSelectedAccount(signInAccount.getAccount());

        return new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName(appName)
                .build();
    }

    public void getBackupData(Drive googleDriveService, GetBackupDataCompleted callback) {
        GetBackupDataTask getBackupDataTask = new GetBackupDataTask(googleDriveService, callback);
        mTaskRunner.run(getBackupDataTask);
    }

    public void restoreDataBaseFromBackup(Drive googleDriveService, String backupFolderId, DB_Costs costsDb, RestoreDataBaseFromBackupProgress progressCallback) {
        RestoreDataBaseFromBackupTask restoreDataBaseFromBackupTask = new RestoreDataBaseFromBackupTask(googleDriveService,
                backupFolderId,
                costsDb,
                progressCallback);
        mTaskRunner.run(restoreDataBaseFromBackupTask);
    }

    public void createDeviceBackup(Drive googleDriveService, String rootFolderId, DB_Costs costsDb, CreateDeviceBackupCompleted callback) {
        CreateDeviceBackupTask createDeviceBackupTask = new CreateDeviceBackupTask(googleDriveService, rootFolderId, costsDb, callback);
        mTaskRunner.run(createDeviceBackupTask);
    }

    public void deleteDeviceBackup(Drive googleDriveService, String backupFolderId, DeleteDeviceBackupCompleted callback) {
        DeleteDeviceBackupTask deleteDeviceBackupTask = new DeleteDeviceBackupTask(googleDriveService, backupFolderId, callback);
        mTaskRunner.run(deleteDeviceBackupTask);
    }

    public void stopTask(int type) {
        mTaskRunner.stopTask(type);
    }

    public int stopCurrentTask() {
        int currentTaskType = mTaskRunner.getCurrentTaskType();
        mTaskRunner.stopTask(currentTaskType);

        return currentTaskType;
    }
}

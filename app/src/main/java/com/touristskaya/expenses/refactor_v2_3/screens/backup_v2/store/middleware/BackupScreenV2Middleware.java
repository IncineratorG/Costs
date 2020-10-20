package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.middleware;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.middleware.Middleware;
import com.touristskaya.expenses.refactor_v2_3.libs.payload.Payload;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.BackupScreenV2Store;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.action_types.BackupScreenV2ActionTypes;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.actions.BackupScreenV2Actions;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.state.BackupScreenV2State;
import com.touristskaya.expenses.refactor_v2_3.services.AppServices;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;
import com.touristskaya.expenses.refactor_v1_active.activities.backup.DataUnitBackupFolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Middleware implements Middleware {
    private BackupScreenV2Store mStore;
    private BackupScreenV2State mState;

    public BackupScreenV2Middleware(BackupScreenV2Store store, BackupScreenV2State state) {
        mStore = store;
        mState = state;
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case (BackupScreenV2ActionTypes.GET_DRIVE_SERVICE): {
                mStore.dispatch(BackupScreenV2Actions.getDriveServiceBeginAction());

                Activity activity = (Activity) action.getPayload();

                GoogleSignInOptions signInOptions =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .requestScopes(new Scope(DriveScopes.DRIVE))
                                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                                .build();
                GoogleSignInClient client = GoogleSignIn.getClient(activity, signInOptions);

                mStore.dispatch(BackupScreenV2Actions.setGoogleSignInClientAction(client));

                activity.startActivityForResult(client.getSignInIntent(), mState.SIGN_IN_REQUEST_CODE);

                break;
            }

            case (BackupScreenV2ActionTypes.ACTIVITY_RESULT): {
                Payload payload = (Payload) action.getPayload();
                int requestCode = (int) payload.get("requestCode");
                int resultCode = (int) payload.get("resultCode");
                Intent resultData = (Intent) payload.get("resultData");
                Activity activity = (Activity) payload.get("activity");
                String appName = (String) payload.get("appName");

                if (requestCode == mState.SIGN_IN_REQUEST_CODE) {
                    if (resultCode == Activity.RESULT_OK) {
                        GoogleSignIn.getSignedInAccountFromIntent(resultData)
                                .addOnSuccessListener(googleSignInAccount -> {
                                    GoogleAccountCredential credential =
                                            GoogleAccountCredential.usingOAuth2(
                                                    activity, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                                    credential.setSelectedAccount(googleSignInAccount.getAccount());

                                    Drive googleDriveService = new Drive.Builder(
                                            AndroidHttp.newCompatibleTransport(),
                                            new GsonFactory(),
                                            credential)
                                            .setApplicationName(appName)
                                            .build();

                                    mStore.dispatch(BackupScreenV2Actions.getDriveServiceFinishedAction(googleDriveService));
                                })
                                .addOnFailureListener(e -> {
                                    SystemEventsHandler.onError("GOOGLE_SIGN_IN_ERROR");
                                    mStore.dispatch(BackupScreenV2Actions.getDriveServiceErrorAction());
                                });
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        mStore.dispatch(BackupScreenV2Actions.getDriveServiceErrorAction());
                    }
                } else {
                    SystemEventsHandler.onError("activityResultHandler(): NOT_HERE");
                }

                break;
            }

            case (BackupScreenV2ActionTypes.GET_BACKUP_DATA): {
                Drive driveService = (Drive) action.getPayload();

                mStore.dispatch(BackupScreenV2Actions.getBackupDataBeginAction());

                AppServices.get().mBackupV2Service.getBackupData(driveService, ((rootFolderId, backupFiles) -> {
                    List<DataUnitBackupFolder> backupFilesList = new ArrayList<>();
                    if (backupFiles != null && backupFiles.getFiles() != null) {
                        for (File file : backupFiles.getFiles()) {
                            DataUnitBackupFolder backupTitle = new DataUnitBackupFolder();
                            backupTitle.setTitle(file.getName());
                            backupTitle.setDriveId(file.getId());

                            backupFilesList.add(backupTitle);
                        }
                    }

                    mStore.dispatch(BackupScreenV2Actions.getBackupDataFinishedAction(backupFilesList));
                }));

                break;
            }
        }
    }
}

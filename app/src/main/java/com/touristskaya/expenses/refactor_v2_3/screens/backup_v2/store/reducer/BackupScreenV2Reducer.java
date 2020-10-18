package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.reducer;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.PropsReducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.PropsState;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.action_types.BackupScreenV2ActionTypes;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.state.BackupScreenV2State;
import com.touristskaya.expenses.refactor_v1_active.activities.backup.DataUnitBackupFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Reducer implements PropsReducer {
    @Override
    public void reduce(PropsState state, Action action) {
        BackupScreenV2State currentState = (BackupScreenV2State) state;
        switch (action.getType()) {
            case (BackupScreenV2ActionTypes.GET_DRIVE_SERVICE_BEGIN): {
                currentState.update(() -> {
                    currentState.acquiringDriveService.set(true);
                    currentState.acquiringDriveServiceHasError.set(false);
                });

                break;
            }

            case (BackupScreenV2ActionTypes.GET_DRIVE_SERVICE_FINISHED): {
                Drive driveService = (Drive) action.getPayload();
                currentState.update(() -> {
                    currentState.googleDriveService.set(driveService);
                    currentState.acquiringDriveService.set(false);
                    currentState.acquiringDriveServiceHasError.set(false);
                });

                break;
            }

            case (BackupScreenV2ActionTypes.GET_DRIVE_SERVICE_ERROR): {
                currentState.update(() -> {
                    currentState.acquiringDriveService.set(false);
                    currentState.acquiringDriveServiceHasError.set(true);
                    currentState.googleDriveService.set(null);
                    currentState.googleDriveService.set(null);
                });

                break;
            }

            case (BackupScreenV2ActionTypes.SET_GOOGLE_SIGN_IN_CLIENT): {
                GoogleSignInClient client = (GoogleSignInClient) action.getPayload();

                currentState.update(() -> {
                    currentState.googleSignInClient.set(client);
                });

                break;
            }

            case (BackupScreenV2ActionTypes.GET_BACKUP_DATA_BEGIN): {
                currentState.update(() -> {
                    currentState.gettingBackupData.set(true);
                    currentState.gettingBackupDataHasError.set(false);
                    currentState.backupDataSet.set(false);
                });

                break;
            }

            case (BackupScreenV2ActionTypes.GET_BACKUP_DATA_FINISHED): {
                List<DataUnitBackupFolder> backupFilesList = (List<DataUnitBackupFolder>) action.getPayload();
                currentState.update(() -> {
                    currentState.gettingBackupData.set(false);
                    currentState.gettingBackupDataHasError.set(false);
                    currentState.backupDataSet.set(true);
                    if (backupFilesList != null) {
                        currentState.backupFilesList.set(backupFilesList);
                    } else {
                        currentState.backupFilesList.set(new ArrayList<>());
                    }
                });

                break;
            }

            case (BackupScreenV2ActionTypes.GET_BACKUP_DATA_ERROR): {
//                SystemEventsHandler.onInfo("GET_BACKUP_DATA_ERROR");

                currentState.update(() -> {
                    currentState.gettingBackupData.set(false);
                    currentState.gettingBackupDataHasError.set(true);
                });

                break;
            }
        }
    }
}

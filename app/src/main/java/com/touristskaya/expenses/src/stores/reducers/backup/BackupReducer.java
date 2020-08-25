package com.touristskaya.expenses.src.stores.reducers.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.reducer.Reducer;
import com.touristskaya.expenses.src.libs.state.State;
import com.touristskaya.expenses.src.stores.states.backup.BackupState;
import com.touristskaya.expenses.src.stores.types.backup.BackupActionTypes;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

/**
 * TODO: Add a class header comment
 */

public class BackupReducer implements Reducer {
    @Override
    public void reduce(State state, Action action) {
        BackupState backupState = (BackupState) state;

        switch (action.getType()) {
            case (BackupActionTypes.CLEAR_BACKUP_STATE): {
                backupState.update(() -> {
                    backupState.signedIn = false;
                    backupState.googleSignInClient = null;
                });
                break;
            }

            case (BackupActionTypes.SET_SIGNED_IN_FLAG): {
                boolean signedIn = (Boolean) action.getPayload();
                backupState.update(() -> backupState.signedIn = signedIn);
                break;
            }

            case (BackupActionTypes.SET_GOOGLE_SIGN_IN_CLIENT): {
                GoogleSignInClient client = (GoogleSignInClient) action.getPayload();
                backupState.update(() -> backupState.googleSignInClient = client);
                break;
            }

            case (BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_BEGIN): {
                backupState.update(() -> {
                    backupState.driveServiceBuilding = true;
                    backupState.driveServiceBuildingHasError = false;
                    backupState.driveServiceBuildingErrorDescription = "";
                });
                break;
            }

            case (BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_FINISHED): {
                SystemEventsHandler.onInfo("BUILD_GOOGLE_DRIVE_SERVICE_FINISHED");

                Drive driveService = (Drive) action.getPayload();
                backupState.update(() -> {
                    backupState.driveService = driveService;
                    backupState.driveServiceBuilding = false;
                    backupState.driveServiceBuildingHasError = false;
                    backupState.driveServiceBuildingErrorDescription = "";
                });
                break;
            }

            case (BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_ERROR): {
                String errorDescription = (String) action.getPayload();

                backupState.update(() -> {
                    backupState.driveServiceBuilding = false;
                    backupState.driveServiceBuildingHasError = true;
                    backupState.driveServiceBuildingErrorDescription = errorDescription == null ? "" : errorDescription;
                });
                break;
            }
        }
    }
}

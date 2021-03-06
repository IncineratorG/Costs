package com.touristskaya.expenses.refactor_v2_3.old_stores.actions.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.payload.Payload;
import com.touristskaya.expenses.refactor_v2_3.old_stores.types.backup.BackupActionTypes;

/**
 * TODO: Add a class header comment
 */

public class BackupActions {
    public static Action clearBackupStateAction() {
        return new Action(BackupActionTypes.CLEAR_BACKUP_STATE);
    }

    public static Action setSignedInFlagAction(boolean signedIn) {
        return new Action(BackupActionTypes.SET_SIGNED_IN_FLAG, signedIn);
    }

    public static Action setGoogleSignInClientAction(GoogleSignInClient client) {
        return new Action(BackupActionTypes.SET_GOOGLE_SIGN_IN_CLIENT, client);
    }

    public static Action buildGoogleDriveServiceAction(Payload payload) {
        return new Action(BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE, payload);
    }

    public static Action buildGoogleDriveServiceBeginAction() {
        return new Action(BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_BEGIN);
    }

    public static Action buildGoogleDriveServiceFinishedAction(Drive driveService) {
        return new Action(BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_FINISHED, driveService);
    }

    public static Action buildGoogleDriveServiceErrorAction(String description) {
        return new Action(BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_ERROR, description);
    }
}

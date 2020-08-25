package com.touristskaya.expenses.src.stores.actions.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.payload.Payload;
import com.touristskaya.expenses.src.stores.types.backup.BackupActionTypes;

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

    public static Action buildGoogleDriveServiceFinishedAction() {
        return new Action(BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_FINISHED);
    }

    public static Action buildGoogleDriveServiceErrorAction(String description) {
        return new Action(BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE_ERROR, description);
    }
}

package com.touristskaya.expenses.refactor_v2_3.screens.backup.store;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenActions {
    public static Action setCurrentActivityAction(Activity activity) {
        return new Action(BackupScreenActionTypes.SET_CURRENT_ACTIVITY, activity);
    }

    public static Action setHasNetworkConnectionAction(boolean hasNetworkConnection) {
        return new Action(BackupScreenActionTypes.SET_HAS_NETWORK_CONNECTION, hasNetworkConnection);
    }

    public static Action setSignedInAction(boolean signedIn) {
        return new Action(BackupScreenActionTypes.SET_SIGNED_IN, signedIn);
    }

    public static Action setGoogleSignInClientAction(GoogleSignInClient client) {
        return new Action(BackupScreenActionTypes.SET_GOOGLE_SIGN_IN_CLIENT, client);
    }
}

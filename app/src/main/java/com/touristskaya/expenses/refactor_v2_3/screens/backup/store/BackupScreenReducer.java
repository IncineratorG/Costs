package com.touristskaya.expenses.refactor_v2_3.screens.backup.store;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.Reducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenReducer implements Reducer {
    @Override
    public void reduce(State state, Action action) {
        BackupScreenState backupScreenState = (BackupScreenState) state;

        switch (action.getType()) {
            case (BackupScreenActionTypes.SET_CURRENT_ACTIVITY): {
                Activity activity = (Activity) action.getPayload();
                backupScreenState.update(() -> backupScreenState.currentActivity = activity);
                break;
            }

            case (BackupScreenActionTypes.SET_HAS_NETWORK_CONNECTION): {
                boolean hasNetworkConnection = (Boolean) action.getPayload();
                backupScreenState.update(() -> backupScreenState.hasNetworkConnection = hasNetworkConnection);
                break;
            }

            case (BackupScreenActionTypes.SET_SIGNED_IN): {
                boolean signedIn = (Boolean) action.getPayload();
                backupScreenState.update(() -> backupScreenState.signedIn = signedIn);
                break;
            }

            case (BackupScreenActionTypes.SET_GOOGLE_SIGN_IN_CLIENT): {
                GoogleSignInClient client = (GoogleSignInClient) action.getPayload();
                backupScreenState.update(() -> backupScreenState.googleSignInClient = client);
                break;
            }
        }
    }
}

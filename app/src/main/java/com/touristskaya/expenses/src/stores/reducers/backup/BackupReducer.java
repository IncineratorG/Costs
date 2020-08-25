package com.touristskaya.expenses.src.stores.reducers.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.reducer.Reducer;
import com.touristskaya.expenses.src.libs.state.State;
import com.touristskaya.expenses.src.stores.states.backup.BackupState;
import com.touristskaya.expenses.src.stores.types.backup.BackupActionTypes;

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
        }
    }
}

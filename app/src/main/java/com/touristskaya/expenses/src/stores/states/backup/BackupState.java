package com.touristskaya.expenses.src.stores.states.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.src.libs.state.State;

public class BackupState extends State {
    public boolean signedIn = false;
    public GoogleSignInClient googleSignInClient = null;
}

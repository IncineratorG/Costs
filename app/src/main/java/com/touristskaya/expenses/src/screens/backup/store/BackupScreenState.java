package com.touristskaya.expenses.src.screens.backup.store;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.src.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenState extends State {
    public final int signInRequestCode = 1;
    public Activity currentActivity = null;
    public boolean hasNetworkConnection = false;
    public boolean signedIn = false;
    public GoogleSignInClient googleSignInClient = null;
}

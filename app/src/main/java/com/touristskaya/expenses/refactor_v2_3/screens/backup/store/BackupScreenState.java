package com.touristskaya.expenses.refactor_v2_3.screens.backup.store;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.state.State;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenState extends State {
    public final int signInRequestCode = 1;
    public Activity currentActivity = null;
    public boolean hasNetworkConnection = false;
    public boolean signedIn = false;
    public GoogleSignInClient googleSignInClient = null;

    public Drive driveService = null;
    public boolean driveServiceBuilding = false;
    public boolean driveServiceBuildingHasError = false;
    public String driveServiceBuildingErrorDescription = "";
}

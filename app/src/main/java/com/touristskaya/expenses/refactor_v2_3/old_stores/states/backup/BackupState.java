package com.touristskaya.expenses.refactor_v2_3.old_stores.states.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.state.State;

public class BackupState extends State {
    public boolean signedIn = false;
    public GoogleSignInClient googleSignInClient = null;

    public Drive driveService = null;
    public boolean driveServiceBuilding = false;
    public boolean driveServiceBuildingHasError = false;
    public String driveServiceBuildingErrorDescription = "";
}

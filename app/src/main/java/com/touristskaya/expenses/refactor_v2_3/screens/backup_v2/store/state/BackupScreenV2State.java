package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.store.state;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.state.PropsState;
import com.touristskaya.expenses.refactor_v2_3.libs.state_prop.StateProp;
import com.touristskaya.expenses.refactor_v2_3.libs.state_prop.StatePropLike;
import com.touristskaya.expenses.refactor_v1_active.activities.backup.DataUnitBackupFolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2State extends PropsState {
    public final int SIGN_IN_REQUEST_CODE = 1;
    public StateProp<Drive> googleDriveService = new StateProp<>(null);
    public StateProp<GoogleSignInClient> googleSignInClient = new StateProp<>(null);
    public StateProp<Boolean> acquiringDriveService = new StateProp<>(false);
    public StateProp<Boolean> acquiringDriveServiceHasError = new StateProp<>(false);

    public StateProp<Boolean> gettingBackupData = new StateProp<>(false);
    public StateProp<Boolean> gettingBackupDataHasError = new StateProp<>(false);
    public StateProp<List<DataUnitBackupFolder>> backupFilesList = new StateProp<>(new ArrayList<>());
    public StateProp<Boolean> backupDataSet = new StateProp<>(false);

    public BackupScreenV2State() {
        initState();
    }

    @Override
    protected List<StatePropLike> stateProps() {
        return Arrays.asList(
                googleDriveService,
                googleSignInClient,
                acquiringDriveService,
                acquiringDriveServiceHasError,
                gettingBackupData,
                gettingBackupDataHasError,
                backupFilesList,
                backupDataSet
        );
    }
}

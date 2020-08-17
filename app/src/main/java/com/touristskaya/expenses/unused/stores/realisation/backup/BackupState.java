package com.touristskaya.expenses.unused.stores.realisation.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.unused.common.types.reactive.realisation.ObservableProperty;
import com.touristskaya.expenses.unused.stores.abstraction.State;
import com.touristskaya.expenses.unused.stores.realisation.backup.types.BackupData;
import com.touristskaya.expenses.unused.stores.realisation.backup.types.CreateDeviceBackupStatus;
import com.touristskaya.expenses.unused.stores.realisation.backup.types.DeleteDeviceBackupStatus;
import com.touristskaya.expenses.unused.stores.realisation.backup.types.DriveServiceBundle;
import com.touristskaya.expenses.unused.stores.realisation.backup.types.RestoreStatus;

/**
 * TODO: Add a class header comment
 */
public class BackupState implements State {
    public ObservableProperty<Boolean> hasInternetConnection = new ObservableProperty<>(false);
    public ObservableProperty<Boolean> signedIn = new ObservableProperty<>(false);
    public ObservableProperty<DriveServiceBundle> driveServiceBundle = new ObservableProperty<>();
    public ObservableProperty<GoogleSignInClient> googleSignInClient = new ObservableProperty<>();
    public ObservableProperty<BackupData> backupData = new ObservableProperty<>();
    public ObservableProperty<RestoreStatus> restoreStatus = new ObservableProperty<>();
    public ObservableProperty<CreateDeviceBackupStatus> createDeviceBackupStatus = new ObservableProperty<>();
    public ObservableProperty<DeleteDeviceBackupStatus> deleteDeviceBackupStatus = new ObservableProperty<>();
}

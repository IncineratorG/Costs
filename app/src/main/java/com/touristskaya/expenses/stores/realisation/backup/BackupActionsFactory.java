package com.touristskaya.expenses.stores.realisation.backup;

import com.touristskaya.expenses.stores.abstraction.Action;
import com.touristskaya.expenses.stores.abstraction.ActionsFactory;
import com.touristskaya.expenses.stores.realisation.backup.actions.BuildGoogleDriveServiceAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.CheckInternetConnectionAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.ClearStoreAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.CreateDeviceBackupAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.DeleteDeviceBackupAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.GetBackupDataAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.RestoreFromBackupAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetBackupDataAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetCreateDeviceBackupStatusAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetDeleteDeviceBackupStatusAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetDriveServiceBundleAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetGoogleSignInClientAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetRestoreStatusAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.SetSignInAction;
import com.touristskaya.expenses.stores.realisation.backup.actions.StopCurrentAsyncTaskAction;

/**
 * TODO: Add a class header comment
 */
public class BackupActionsFactory implements ActionsFactory {
    public static final int CheckInternetConnection = 1;
    public static final int SetGoogleSignInClient = 2;
    public static final int SetSignIn = 3;
    public static final int BuildGoogleDriveService = 4;
    public static final int SetDriveServiceBundle = 5;
    public static final int ClearStore = 6;
    public static final int GetBackupData = 7;
    public static final int SetBackupData = 8;
    public static final int StopCurrentAsyncTask = 9;
    public static final int RestoreFromBackup = 10;
    public static final int SetRestoreStatus = 11;
    public static final int CreateDeviceBackup = 12;
    public static final int SetCreateDeviceBackupStatus = 13;
    public static final int DeleteDeviceBackup = 14;
    public static final int SetDeleteDeviceBackupStatus = 15;


    @Override
    public Action getAction(int type) {
        switch (type) {
            case CheckInternetConnection: {
                return new CheckInternetConnectionAction();
            }

            case SetGoogleSignInClient: {
                return new SetGoogleSignInClientAction();
            }

            case SetSignIn: {
                return new SetSignInAction();
            }

            case BuildGoogleDriveService: {
                return new BuildGoogleDriveServiceAction();
            }

            case SetDriveServiceBundle: {
                return new SetDriveServiceBundleAction();
            }

            case ClearStore: {
                return new ClearStoreAction();
            }

            case GetBackupData: {
                return new GetBackupDataAction();
            }

            case SetBackupData: {
                return new SetBackupDataAction();
            }

            case StopCurrentAsyncTask: {
                return new StopCurrentAsyncTaskAction();
            }

            case RestoreFromBackup: {
                return new RestoreFromBackupAction();
            }

            case SetRestoreStatus: {
                return new SetRestoreStatusAction();
            }

            case CreateDeviceBackup: {
                return new CreateDeviceBackupAction();
            }

            case SetCreateDeviceBackupStatus: {
                return new SetCreateDeviceBackupStatusAction();
            }

            case DeleteDeviceBackup: {
                return new DeleteDeviceBackupAction();
            }

            case SetDeleteDeviceBackupStatus: {
                return new SetDeleteDeviceBackupStatusAction();
            }
        }

        return null;
    }
}

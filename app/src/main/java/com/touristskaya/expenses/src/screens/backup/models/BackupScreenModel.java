package com.touristskaya.expenses.src.screens.backup.models;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.selector.Selector;
import com.touristskaya.expenses.src.libs.state.State;
import com.touristskaya.expenses.src.libs.store.Store;
import com.touristskaya.expenses.src.libs.void_function.VoidFunction;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenActions;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenReducer;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenState;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.old_stores.OldAppStore;
import com.touristskaya.expenses.src.old_stores.actions.system.SystemActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenModel {
    private BackupScreenState mState;
    private BackupScreenReducer mReducer;
    private Dispatcher mDispatcher;
    private List<VoidFunction> mSubscriptions;

    public BackupScreenModel(Activity activity) {
        mState = new BackupScreenState();
        mReducer = new BackupScreenReducer();
        mDispatcher = new Store(mState, mReducer).getDispatcher();
        mDispatcher.dispatch(BackupScreenActions.setCurrentActivityAction(activity));

        OldAppStore.init();
        AppServices.getInstance().init();

        mSubscriptions = new ArrayList<>(
                Arrays.asList(
                        OldAppStore.systemState.select(new Selector((selector) -> {
                            Boolean prevHasNetworkConnection = (Boolean) selector.getPrevValue("hasNetworkConnection");
                            boolean currHasNetworkConnection = OldAppStore.systemState.hasNetworkConnection;

                            if (prevHasNetworkConnection == null || prevHasNetworkConnection != currHasNetworkConnection) {
                                mDispatcher.dispatch(BackupScreenActions.setHasNetworkConnectionAction(currHasNetworkConnection));
                            }

                            selector.setPrevValue("hasNetworkConnection", currHasNetworkConnection);
                        })),
                        OldAppStore.backupState.select(new Selector((selector) -> {
                            Boolean prevSignedIn = (Boolean) selector.getPrevValue("signedIn");
                            boolean currSignedIn = OldAppStore.backupState.signedIn;

                            if (prevSignedIn == null || prevSignedIn != currSignedIn) {
                                mDispatcher.dispatch(BackupScreenActions.setSignedInAction(currSignedIn));
                            }

                            selector.setPrevValue("signedIn", currSignedIn);
                        })),
                        OldAppStore.backupState.select(new Selector((selector) -> {
                            GoogleSignInClient prevGoogleClient = (GoogleSignInClient) selector.getPrevValue("googleSignInClient");
                            GoogleSignInClient currGoogleClient = OldAppStore.backupState.googleSignInClient;

                            if (prevGoogleClient == null || prevGoogleClient != currGoogleClient) {
                                mDispatcher.dispatch(BackupScreenActions.setGoogleSignInClientAction(currGoogleClient));
                            }

                            selector.setPrevValue("googleSignInClient", currGoogleClient);
                        })),
                        OldAppStore.backupState.select(new Selector((selector) -> {
                            Drive prevDriveService = (Drive) selector.getPrevValue("driveService");
                            Boolean prevDriveServiceBuilding = (Boolean) selector.getPrevValue("driveServiceBuilding");
                            Boolean prevDriveServiceBuildingHasError = (Boolean) selector.getPrevValue("driveServiceBuildingHasError");
                            String prevDriveServiceBuildingErrorDescription = (String) selector.getPrevValue("driveServiceBuildingErrorDescription");

                            Drive currDriveService = OldAppStore.backupState.driveService;
                            boolean currDriveServiceBuilding = OldAppStore.backupState.driveServiceBuilding;
                            boolean currDriveServiceBuildingHasError = OldAppStore.backupState.driveServiceBuildingHasError;
                            String currDriveServiceBuildingErrorDescription = OldAppStore.backupState.driveServiceBuildingErrorDescription;

                            if (prevDriveService == null || prevDriveService != currDriveService) {
                                mState.update(() -> mState.driveService = currDriveService);
                            }
                            if (prevDriveServiceBuilding == null || prevDriveServiceBuilding != currDriveServiceBuilding) {
                                mState.update(() -> mState.driveServiceBuilding = currDriveServiceBuilding);
                            }
                            if (prevDriveServiceBuildingHasError == null || prevDriveServiceBuildingHasError != currDriveServiceBuildingHasError) {
                                mState.update(() -> mState.driveServiceBuildingHasError = currDriveServiceBuildingHasError);
                            }
                            if (prevDriveServiceBuildingErrorDescription == null || !prevDriveServiceBuildingErrorDescription.equals(currDriveServiceBuildingErrorDescription)) {
                                mState.update(() -> mState.driveServiceBuildingErrorDescription = currDriveServiceBuildingErrorDescription);
                            }

                            selector.setPrevValue("driveService", currDriveService);
                            selector.setPrevValue("driveServiceBuilding", currDriveServiceBuilding);
                            selector.setPrevValue("driveServiceBuildingHasError", currDriveServiceBuildingHasError);
                            selector.setPrevValue("driveServiceBuildingErrorDescription", currDriveServiceBuildingErrorDescription);
                        }))
                )
        );

        OldAppStore.dispatch(SystemActions.updateNetworkConnectionAction(mState.currentActivity));
    }



    public State getState() {
        return mState;
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }

    public void unsubscribe() {
        for (VoidFunction unsubscribe : mSubscriptions) {
            unsubscribe.invoke();
        }
    }
}

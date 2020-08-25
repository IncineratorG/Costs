package com.touristskaya.expenses.src.screens.backup.models;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.touristskaya.expenses.src.libs.dispatcher.Dispatcher;
import com.touristskaya.expenses.src.libs.selector.Selector;
import com.touristskaya.expenses.src.libs.state.State;
import com.touristskaya.expenses.src.libs.store.Store;
import com.touristskaya.expenses.src.libs.void_function.VoidFunction;
import com.touristskaya.expenses.src.screens.backup.BackupScreen;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenActions;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenReducer;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenState;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.stores.AppStore;
import com.touristskaya.expenses.src.stores.actions.system.SystemActions;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

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

        AppStore.init();
        AppServices.getInstance().init();

        mSubscriptions = new ArrayList<>(
                Arrays.asList(
                        AppStore.systemState.select(new Selector((selector) -> {
                            Boolean prevHasNetworkConnection = (Boolean) selector.getPrevValue("hasNetworkConnection");
                            boolean currHasNetworkConnection = AppStore.systemState.hasNetworkConnection;

                            if (prevHasNetworkConnection == null || prevHasNetworkConnection != currHasNetworkConnection) {
                                mDispatcher.dispatch(BackupScreenActions.setHasNetworkConnectionAction(currHasNetworkConnection));
                            }

                            selector.setPrevValue("hasNetworkConnection", currHasNetworkConnection);
                        })),
                        AppStore.backupState.select(new Selector((selector) -> {
                            Boolean prevSignedIn = (Boolean) selector.getPrevValue("signedIn");
                            boolean currSignedIn = AppStore.backupState.signedIn;

                            if (prevSignedIn == null || prevSignedIn != currSignedIn) {
                                mDispatcher.dispatch(BackupScreenActions.setSignedInAction(currSignedIn));
                            }

                            selector.setPrevValue("signedIn", currSignedIn);
                        })),
                        AppStore.backupState.select(new Selector((selector) -> {
                            GoogleSignInClient prevGoogleClient = (GoogleSignInClient) selector.getPrevValue("googleSignInClient");
                            GoogleSignInClient currGoogleClient = AppStore.backupState.googleSignInClient;

                            if (prevGoogleClient == null || prevGoogleClient != currGoogleClient) {
                                mDispatcher.dispatch(BackupScreenActions.setGoogleSignInClientAction(currGoogleClient));
                            }

                            selector.setPrevValue("googleSignInClient", currGoogleClient);
                        }))
                )
        );

        AppStore.dispatch(SystemActions.updateNetworkConnectionAction(mState.currentActivity));
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

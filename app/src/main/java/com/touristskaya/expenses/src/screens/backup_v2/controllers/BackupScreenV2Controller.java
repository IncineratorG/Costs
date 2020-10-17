package com.touristskaya.expenses.src.screens.backup_v2.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.touristskaya.expenses.ActivityMainWithFragments;
import com.touristskaya.expenses.src.screens.backup_v2.models.BackupScreenV2Model;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

import java.util.Collections;

/**
 * TODO: Add a class header comment
 */

public class BackupScreenV2Controller {
    private BackupScreenV2Model mModel;

    public BackupScreenV2Controller(BackupScreenV2Model model) {
        mModel = model;
    }

    public boolean hasNetworkConnectionHandler() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mModel.currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            mModel.hasNetworkConnection.set(networkInfo != null);
            return networkInfo != null;
        } else {
            mModel.hasNetworkConnection.set(false);
            return false;
        }
    }

    public void backButtonHandler() {
        Intent mainActivityWithFragmentsIntent = new Intent(mModel.currentActivity, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mModel.currentActivity.startActivity(mainActivityWithFragmentsIntent);
    }

    public void requestSignInHandler() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(mModel.currentActivity, signInOptions);

        mModel.currentActivity.startActivityForResult(client.getSignInIntent(), mModel.SIGN_IN_REQUEST_CODE);
    }

    public void activityResultHandler(int requestCode, int resultCode, Intent resultData) {
        SystemEventsHandler.onInfo("activityResultHandler(): " + requestCode + " - " + resultCode + " - " + (resultData == null));

        if (requestCode == mModel.SIGN_IN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                SystemEventsHandler.onInfo("activityResultHandler(): OK");
                buildGoogleDriveService(resultData);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                SystemEventsHandler.onInfo("activityResultHandler(): CANCELLED");
            }
        } else {
            SystemEventsHandler.onError("activityResultHandler(): NOT_HERE");
        }
    }

    private void buildGoogleDriveService(Intent intent) {
        GoogleSignIn.getSignedInAccountFromIntent(intent)
                .addOnSuccessListener(googleSignInAccount -> {
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    mModel.currentActivity, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                    credential.setSelectedAccount(googleSignInAccount.getAccount());

                    mModel.googleDriveService.set(
                            new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName(mModel.appName)
                                .build()
                    );
                })
                .addOnFailureListener(e -> {
                    SystemEventsHandler.onError("GOOGLE_SIGN_IN_ERROR");
                });
    }
}

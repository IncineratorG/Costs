package com.touristskaya.expenses.src.services.backup;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.touristskaya.expenses.src.libs.promise.Promise;
import com.touristskaya.expenses.src.libs.service.Service;

import java.util.Collections;

/**
 * TODO: Add a class header comment
 */

public class BackupService implements Service {
    public BackupService() {
    }

    public void init() {}

    public Promise<Drive> buildGoogleDriveService(Intent intent, Context context, String appName) {
        Promise<Drive> promise = new Promise<>();

        GoogleSignIn.getSignedInAccountFromIntent(intent)
                .addOnSuccessListener(googleSignInAccount -> {
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                    credential.setSelectedAccount(googleSignInAccount.getAccount());

                    Drive drive = new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName(appName)
                            .build();

                    promise.resolve(drive);
                })
                .addOnFailureListener(e -> {
                    promise.reject("GOOGLE_SIGN_IN_ERROR");
                });

        return promise;
    }
}

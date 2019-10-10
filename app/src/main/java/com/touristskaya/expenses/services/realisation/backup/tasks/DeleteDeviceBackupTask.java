package com.touristskaya.expenses.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.services.realisation.backup.callbacks.DeleteDeviceBackupCompleted;

import java.io.IOException;

/**
 * TODO: Add a class header comment
 */
public class DeleteDeviceBackupTask extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService = null;
    private String mBackupFolderId = null;
    private DeleteDeviceBackupCompleted mCompletionCallback;


    public DeleteDeviceBackupTask(Drive googleDriveService, String backupFolderId, DeleteDeviceBackupCompleted callback) {
        mGoogleDriveService = googleDriveService;
        mBackupFolderId = backupFolderId;
        mCompletionCallback = callback;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        if (mGoogleDriveService == null || mBackupFolderId == null || mBackupFolderId.isEmpty()) {
            return null;
        }

        try {
            mGoogleDriveService.files().delete(mBackupFolderId).execute();
        } catch (IOException e) {
            Log.d(TAG, "DeleteDeviceBackupTask.doInBackground()->IOEXCEPTION: " + e.getMessage());
            return null;
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        mCompletionCallback.complete(false);
    }

    @Override
    protected void onPostExecute(Object o) {
        mCompletionCallback.complete(true);
    }
}

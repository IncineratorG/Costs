package com.touristskaya.expenses.src.old_stores.middleware.backup.handlers;

import android.app.Activity;
import android.content.Intent;

import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.payload.Payload;
import com.touristskaya.expenses.src.libs.promise.Promise;
import com.touristskaya.expenses.src.services.AppServices;
import com.touristskaya.expenses.src.services.backup.BackupService;
import com.touristskaya.expenses.src.old_stores.OldAppStore;
import com.touristskaya.expenses.src.old_stores.actions.backup.BackupActions;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;

public class BackupMiddlewareHandlers {
    public void buildGoogleDriveHandler(Action action) {
        SystemEventsHandler.onInfo("buildGoogleDriveHandler()");

        OldAppStore.dispatch(BackupActions.buildGoogleDriveServiceBeginAction());

        Payload payload = (Payload) action.getPayload();
        Intent intent = (Intent) payload.get("resultIntent");
        Activity activity = (Activity) payload.get("context");
        String appLabel = (String) payload.get("appLabel");

        if (intent == null || activity == null || appLabel == null) {
            SystemEventsHandler.onError("buildGoogleDriveHandler()->BAD_INPUT_DATA");
            OldAppStore.dispatch(BackupActions.buildGoogleDriveServiceErrorAction("BAD_INPUT_DATA"));
            return;
        }

        BackupService backupService = (BackupService) AppServices.getInstance().get(AppServices.BACKUP_SERVICE);
        Promise<Drive> promise = backupService.buildGoogleDriveService(intent, activity, appLabel);
        promise.then(drive -> {
            if (drive == null) {
                OldAppStore.dispatch(BackupActions.buildGoogleDriveServiceErrorAction("DRIVE_SERVICE_IS_NULL"));
                return;
            }

            OldAppStore.dispatch(BackupActions.buildGoogleDriveServiceFinishedAction(drive));
        });
        promise.error(errorText -> {
            SystemEventsHandler.onError("buildGoogleDriveHandler()->PROMISE_ERROR: " + errorText);
        });
    }
}

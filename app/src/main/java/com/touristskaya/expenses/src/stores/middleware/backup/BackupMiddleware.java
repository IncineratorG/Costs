package com.touristskaya.expenses.src.stores.middleware.backup;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.middleware.Middleware;
import com.touristskaya.expenses.src.stores.middleware.backup.handlers.BackupMiddlewareHandlers;
import com.touristskaya.expenses.src.stores.types.backup.BackupActionTypes;

public class BackupMiddleware implements Middleware {
    private BackupMiddlewareHandlers mBackupHandlers = new BackupMiddlewareHandlers();

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case (BackupActionTypes.BUILD_GOOGLE_DRIVE_SERVICE): {
                mBackupHandlers.buildGoogleDriveHandler(action);
                break;
            }
        }
    }
}

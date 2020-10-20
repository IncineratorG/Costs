package com.touristskaya.expenses.refactor_v2_3.old_stores.middleware.backup;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.middleware.Middleware;
import com.touristskaya.expenses.refactor_v2_3.old_stores.middleware.backup.handlers.BackupMiddlewareHandlers;
import com.touristskaya.expenses.refactor_v2_3.old_stores.types.backup.BackupActionTypes;

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

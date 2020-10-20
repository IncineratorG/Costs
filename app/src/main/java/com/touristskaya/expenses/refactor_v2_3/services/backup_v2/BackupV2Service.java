package com.touristskaya.expenses.refactor_v2_3.services.backup_v2;

import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.refactor_v2_3.libs.service.Service;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.callbacks.GetBackupDataCompleted;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.GetBackupDataTask;
import com.touristskaya.expenses.refactor_v1_active.services.realisation.backup.tasks.TaskRunner;

/**
 * TODO: Add a class header comment
 */

public class BackupV2Service implements Service {
    private TaskRunner mTaskRunner;

    public BackupV2Service() {
        mTaskRunner = TaskRunner.getInstance();
    }

    public void getBackupData(Drive googleDriveService, GetBackupDataCompleted callback) {
        GetBackupDataTask getBackupDataTask = new GetBackupDataTask(googleDriveService, callback);
        mTaskRunner.run(getBackupDataTask);
    }
}

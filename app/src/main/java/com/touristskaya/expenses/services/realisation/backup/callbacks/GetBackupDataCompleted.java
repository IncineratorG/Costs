package com.touristskaya.expenses.services.realisation.backup.callbacks;

import com.google.api.services.drive.model.FileList;

/**
 * TODO: Add a class header comment
 */
public interface GetBackupDataCompleted {
    void complete(String rootFolderId, FileList backupFiles);
}

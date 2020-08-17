package com.touristskaya.expenses.unused.services.realisation.backup.callbacks;

import com.google.api.services.drive.model.FileList;

/**
 * TODO: Add a class header comment
 */
public interface GetBackupDataCompleted {
    void complete(String rootFolderId, FileList backupFiles);
}

package com.touristskaya.expenses.unused.stores.realisation.backup.types;

/**
 * TODO: Add a class header comment
 */
public class CreateDeviceBackupStatus {
    private String mStatus;
    private String mDeviceBackupFolderId;

    public static final String InProgress = "in_progress";
    public static final String Complete = "complete";
    public static final String NotComplete = "not_complete";

    public CreateDeviceBackupStatus(String s, String backupFolderId) {
        this.mStatus = s;
        if (backupFolderId == null) {
            this.mDeviceBackupFolderId = "";
        } else {
            this.mDeviceBackupFolderId = backupFolderId;
        }
    }

    public String getStatus() {
        return mStatus;
    }

    public String getBackupFolderId() {
        return mDeviceBackupFolderId;
    }
}

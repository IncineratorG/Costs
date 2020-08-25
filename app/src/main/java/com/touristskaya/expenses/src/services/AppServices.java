package com.touristskaya.expenses.src.services;

import com.touristskaya.expenses.src.libs.service.Service;
import com.touristskaya.expenses.src.services.backup.BackupService;
import com.touristskaya.expenses.src.services.system.SystemService;

public class AppServices {
    public static final String SYSTEM_SERVICE = "SYSTEM_SERVICE";
    public static final String BACKUP_SERVICE = "BACKUP_SERVICE";

    private static AppServices mInstance = null;

    private SystemService mSystemService;
    private BackupService mBackupService;

    private AppServices() {
        mSystemService = new SystemService();
        mBackupService = new BackupService();
    }

    public static synchronized AppServices getInstance() {
        if (mInstance == null) {
            mInstance = new AppServices();
        }

        return mInstance;
    }

    public void init() {
        mSystemService.init();
        mBackupService.init();
    }

    public Service get(String serviceType) {
        switch (serviceType) {
            case SYSTEM_SERVICE: {
                return mSystemService;
            }

            case BACKUP_SERVICE: {
                return mBackupService;
            }

            default: {
                return null;
            }
        }
    }
}

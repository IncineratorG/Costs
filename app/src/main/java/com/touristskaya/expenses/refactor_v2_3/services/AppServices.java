package com.touristskaya.expenses.refactor_v2_3.services;

import com.touristskaya.expenses.refactor_v2_3.libs.service.Service;
import com.touristskaya.expenses.refactor_v2_3.services.backup.BackupService;
import com.touristskaya.expenses.refactor_v2_3.services.backup_v2.BackupV2Service;
import com.touristskaya.expenses.refactor_v2_3.services.system.SystemService;

public class AppServices {
    public static final String SYSTEM_SERVICE = "SYSTEM_SERVICE";
    public static final String BACKUP_SERVICE = "BACKUP_SERVICE";

    private static AppServices mInstance = null;

    public SystemService mSystemService;
    public BackupService mBackupService;
    public BackupV2Service mBackupV2Service;

    private AppServices() {
        mSystemService = new SystemService();
        mBackupService = new BackupService();
        mBackupV2Service = new BackupV2Service();
    }

    public static synchronized AppServices get() {
        if (mInstance == null) {
            mInstance = new AppServices();
        }

        return mInstance;
    }

    public void init() {
        mSystemService.init();
        mBackupService.init();
    }

    public Service getService(String serviceType) {
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

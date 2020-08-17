package com.touristskaya.expenses.unused.stores.realisation;

import com.touristskaya.expenses.unused.stores.abstraction.Store;
import com.touristskaya.expenses.unused.stores.realisation.backup.BackupStore;

/**
 * TODO: Add a class header comment
 */
public class Stores {
    private static Stores mInstance = null;
    private com.touristskaya.expenses.unused.stores.realisation.backup.BackupStore mBackupStore;

    public static final int BackupStore = 2;


    private Stores() {
        mBackupStore = new BackupStore();
    }

    public static synchronized Stores getInstance() {
        if (mInstance != null)
            return mInstance;
        else {
            mInstance = new Stores();
            return mInstance;
        }
    }

    public Store getStore(int type) {
        switch (type) {
            case BackupStore: {
                return mBackupStore;
            }
        }

        return null;
    }
}

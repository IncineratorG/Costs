package com.touristskaya.expenses.unused.stores.realisation.backup.actions;

import com.touristskaya.expenses.unused.stores.abstraction.Action;
import com.touristskaya.expenses.unused.stores.realisation.backup.BackupActionsFactory;

/**
 * TODO: Add a class header comment
 */
public class SetDeleteDeviceBackupStatusAction implements Action {
    private Object mPayload;


    @Override
    public int getType() {
        return BackupActionsFactory.SetDeleteDeviceBackupStatus;
    }

    @Override
    public Object getPayload() {
        return mPayload;
    }

    @Override
    public void setPayload(Object payload) {
        mPayload = payload;
    }
}
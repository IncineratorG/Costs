package com.touristskaya.expenses.refactor_v1_active.stores.realisation.backup.actions;

import com.touristskaya.expenses.refactor_v1_active.stores.abstraction.Action;
import com.touristskaya.expenses.refactor_v1_active.stores.realisation.backup.BackupActionsFactory;

/**
 * TODO: Add a class header comment
 */
public class GetBackupDataAction implements Action {
    private Object mPayload;


    @Override
    public int getType() {
        return BackupActionsFactory.GetBackupData;
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

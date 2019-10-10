package com.touristskaya.expenses.stores.realisation.backup.actions;

import com.touristskaya.expenses.stores.abstraction.Action;
import com.touristskaya.expenses.stores.realisation.backup.BackupActionsFactory;

/**
 * TODO: Add a class header comment
 */
public class SetDriveServiceBundleAction implements Action {
    private Object mPayload;


    @Override
    public int getType() {
        return BackupActionsFactory.SetDriveServiceBundle;
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

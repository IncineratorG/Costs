package com.touristskaya.expenses.refactor_v2_3.old_stores.actions.system;

import android.content.Context;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.old_stores.types.system.SystemActionTypes;

/**
 * TODO: Add a class header comment
 */

public class SystemActions {
    public static Action updateNetworkConnectionAction(Context context) {
        return new Action(SystemActionTypes.UPDATE_NETWORK_CONNECTION, context);
    }

    public static Action setHasNetworkConnectionAction(boolean hasConnection) {
        return new Action(SystemActionTypes.SET_HAS_NETWORK_CONNECTION, hasConnection);
    }
}

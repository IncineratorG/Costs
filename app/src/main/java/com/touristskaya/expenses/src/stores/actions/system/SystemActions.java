package com.touristskaya.expenses.src.stores.actions.system;

import android.content.Context;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.stores.types.system.SystemActionTypes;

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

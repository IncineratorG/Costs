package com.touristskaya.expenses.src.stores.actions.system;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.stores.action_types.system.SystemActionTypes;

/**
 * TODO: Add a class header comment
 */

public class SystemActions {
    public static Action setHasNetworkConnectionAction(boolean hasConnection) {
        return new Action(SystemActionTypes.SET_HAS_NETWORK_CONNECTION, hasConnection);
    }
}

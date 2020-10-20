package com.touristskaya.expenses.refactor_v2_3.stores.actions.system;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.stores.action_types.system.SystemActionTypes;

/**
 * TODO: Add a class header comment
 */

public class SystemActions {
    public static Action setHasNetworkConnectionAction(boolean hasConnection) {
        return new Action(SystemActionTypes.SET_HAS_NETWORK_CONNECTION, hasConnection);
    }
}

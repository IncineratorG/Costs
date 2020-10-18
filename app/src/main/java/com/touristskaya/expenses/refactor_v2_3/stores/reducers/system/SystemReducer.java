package com.touristskaya.expenses.refactor_v2_3.stores.reducers.system;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.PropsReducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.PropsState;
import com.touristskaya.expenses.refactor_v2_3.stores.action_types.system.SystemActionTypes;
import com.touristskaya.expenses.refactor_v2_3.stores.states.system.SystemState;

/**
 * TODO: Add a class header comment
 */

public class SystemReducer implements PropsReducer {
    @Override
    public void reduce(PropsState state, Action action) {
        SystemState systemState = (SystemState) state;

        switch (action.getType()) {
            case (SystemActionTypes.SET_HAS_NETWORK_CONNECTION): {
                boolean hasNetworkConnection = (boolean) action.getPayload();

                systemState.update(() -> {
                    systemState.hasNetworkConnection.set(hasNetworkConnection);
                });

                break;
            }
        }
    }
}

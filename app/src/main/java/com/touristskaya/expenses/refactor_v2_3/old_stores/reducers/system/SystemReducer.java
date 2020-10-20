package com.touristskaya.expenses.refactor_v2_3.old_stores.reducers.system;

import com.touristskaya.expenses.refactor_v2_3.libs.action.Action;
import com.touristskaya.expenses.refactor_v2_3.libs.reducer.Reducer;
import com.touristskaya.expenses.refactor_v2_3.libs.state.State;
import com.touristskaya.expenses.refactor_v2_3.old_stores.states.system.SystemState;
import com.touristskaya.expenses.refactor_v2_3.old_stores.types.system.SystemActionTypes;

/**
 * TODO: Add a class header comment
 */

public class SystemReducer implements Reducer {
    @Override
    public void reduce(State state, Action action) {
        SystemState systemState = (SystemState) state;

        switch (action.getType()) {
            case (SystemActionTypes.SET_HAS_NETWORK_CONNECTION): {
                boolean hasNetworkConnection = (boolean) action.getPayload();

                systemState.update(() -> systemState.hasNetworkConnection = hasNetworkConnection);

                break;
            }
        }
    }
}

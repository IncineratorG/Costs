package com.touristskaya.expenses.src.stores.reducers.system;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.reducer.Reducer;
import com.touristskaya.expenses.src.libs.state.State;
import com.touristskaya.expenses.src.stores.states.system.SystemState;
import com.touristskaya.expenses.src.stores.types.system.SystemActionTypes;

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

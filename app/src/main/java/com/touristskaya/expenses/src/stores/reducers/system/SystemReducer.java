package com.touristskaya.expenses.src.stores.reducers.system;

import android.util.Log;

import com.touristskaya.expenses.src.libs.action.Action;
import com.touristskaya.expenses.src.libs.reducer.PropsReducer;
import com.touristskaya.expenses.src.libs.state.PropsState;
import com.touristskaya.expenses.src.stores.action_types.system.SystemActionTypes;
import com.touristskaya.expenses.src.stores.states.system.SystemState;

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

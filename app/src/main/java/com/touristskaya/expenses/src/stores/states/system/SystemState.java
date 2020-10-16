package com.touristskaya.expenses.src.stores.states.system;

import com.touristskaya.expenses.src.libs.state.PropsState;
import com.touristskaya.expenses.src.libs.state_prop.StateProp;
import com.touristskaya.expenses.src.libs.state_prop.StatePropLike;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class SystemState extends PropsState {
    public StateProp<Boolean> hasNetworkConnection = new StateProp<>(false);

    public SystemState() {
        initState();
    }

    @Override
    protected List<StatePropLike> stateProps() {
        return Arrays.asList(
                hasNetworkConnection
        );
    }
}

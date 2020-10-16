package com.touristskaya.expenses.src.libs.selector;

import com.touristskaya.expenses.src.libs.state_prop.StatePropLike;
import com.touristskaya.expenses.src.libs.void_function.VoidFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class PropsSelector {
    private int mId;
    private List<Integer> mPropIds;
    private VoidFunction mSelectorFunc;

    public PropsSelector(List<StatePropLike> props, VoidFunction selectorFunc) {
        mPropIds = new ArrayList<>();

        mSelectorFunc = selectorFunc;
        for (int i = 0; i < props.size(); ++i) {
            mPropIds.add(props.get(i).propId());
        }

        mId = SelectorIdsGenerator.nextId();
    }

    public int id() {
        return mId;
    }

    public List<Integer> propIds() {
        return mPropIds;
    }

    public VoidFunction selectorFunc() {
        return mSelectorFunc;
    }
}

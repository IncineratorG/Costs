package com.touristskaya.expenses.refactor_v2_3.libs.state_prop;

/**
 * TODO: Add a class header comment
 */

public class StatePropLike {
    private int mPropId;
    private boolean mUpdated;

    public StatePropLike() {
        mPropId = -1;
        mUpdated = false;
    }

    public void setPropId(int id) {
        mPropId = id;
    }
    public void setUpdated(boolean updated) {
        mUpdated = updated;
    }


    public int propId() {
        return mPropId;
    }
    public boolean updated() {
        return mUpdated;
    }
}

package com.touristskaya.expenses.src.libs.action;

import java.util.UUID;

/**
 * TODO: Add a class header comment
 */

public class Action {
    private String mType;
    private Object mPayload;
    private UUID mUuid;

    public Action(String type) {
        this.mType = type;
        this.mPayload = null;
        mUuid = UUID.randomUUID();
    }

    public Action(String type, Object payload) {
        this.mType = type;
        this.mPayload = payload;
        mUuid = UUID.randomUUID();
    }

    public Action(Action other) {
        mType = other.mType;
        mPayload = other.mPayload;
        mUuid = other.mUuid;
    }

    public UUID getUuid() {
        return mUuid;
    }

    public String getType() {
        return mType;
    }

    public Object getPayload() {
        return mPayload;
    }
}

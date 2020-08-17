package com.touristskaya.expenses.src.utils.common.notifier;

import androidx.core.util.Consumer;

/**
 * TODO: Add a class header comment
 */

public class Subscription {
    private int mId;
    private String mEvent;
    private Consumer<Object> mHandler;

    public Subscription(int id, String event, Consumer<Object> handler) {
        mId = id;
        mEvent = event;
        mHandler = handler;
    }

    public int getId() {
        return mId;
    }

    public String getEvent() {
        return mEvent;
    }

    public Consumer<Object> getHandler() {
        return mHandler;
    }
}

package com.touristskaya.expenses.unused.stores.abstraction;

/**
 * TODO: Add a class header comment
 */
public interface Action {
    int getType();
    Object getPayload();
    void setPayload(Object payload);
}

package com.touristskaya.expenses.stores.abstraction;

/**
 * TODO: Add a class header comment
 */
public interface Action {
    int getType();
    Object getPayload();
    void setPayload(Object payload);
}

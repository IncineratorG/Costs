package com.touristskaya.expenses.refactor_v1_active.stores.abstraction;

/**
 * TODO: Add a class header comment
 */
public interface Action {
    int getType();
    Object getPayload();
    void setPayload(Object payload);
}

package com.touristskaya.expenses.refactor_v1_active.common.types.reactive.realisation;

import com.touristskaya.expenses.refactor_v1_active.common.types.reactive.abstraction.Executable;

/**
 * TODO: Add a class header comment
 */
public class Subscriber {
    private Subscription mSubscription;
    private Executable mExecutable;


    public Subscriber(Subscription s, Executable e) {
        this.mSubscription = s;
        this.mExecutable = e;
    }

    public Subscription getSubscription() {
        return mSubscription;
    }

    public  Executable getExecutable() {
        return mExecutable;
    }
}

package com.touristskaya.expenses.src.utils.common.notifier;

import androidx.core.util.Consumer;

import com.touristskaya.expenses.src.libs.void_function.VoidFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class Notifier {
    private int mIdsCounter = 1;
    private List<Subscription> mSubscriptions;

    public Notifier() {
        mSubscriptions = new ArrayList<>();
    }

    public VoidFunction subscribe(String event, Consumer<Object> handler) {
        int subscriptionId = ++mIdsCounter;

        Subscription s = new Subscription(subscriptionId, event, handler);

        mSubscriptions.add(s);

        return () -> {
            for (int i = 0; i < mSubscriptions.size(); ++i) {
                if (mSubscriptions.get(i).getId() == subscriptionId) {
                    mSubscriptions.remove(i);
                    break;
                }
            }
        };
    }

    public void notifySubscribers(String event, Object data) {
        for (Subscription s : mSubscriptions) {
            if (s.getEvent().equals(event)) {
                s.getHandler().accept(data);
            }
        }
    }
}

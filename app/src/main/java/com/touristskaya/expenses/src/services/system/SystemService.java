package com.touristskaya.expenses.src.services.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.util.Consumer;

import com.touristskaya.expenses.src.libs.service.Service;
import com.touristskaya.expenses.src.libs.void_function.VoidFunction;
import com.touristskaya.expenses.src.services.system.data.event_types.SystemServiceEvents;
import com.touristskaya.expenses.src.utils.common.notifier.Notifier;

public class SystemService implements Service {
    private Notifier mNotifier;

    public SystemService() {
        mNotifier = new Notifier();
    }

    public void init() {

    }

    public VoidFunction subscribe(String event, Consumer<Object> handler) {
        return mNotifier.subscribe(event, handler);
    }

    public void updateNetworkConnectionInfo(Context context) {
        if (context == null) {
            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean hasNetworkConnection = networkInfo != null;

        mNotifier.notifySubscribers(SystemServiceEvents.NETWORK_CONNECTION_CHANGED, hasNetworkConnection);
    }

//    public void test() {
//        String testEventResult = "TEST_EVENT_RESULT";
//
//        mNotifier.notifySubscribers(SystemServiceEvents.TEST_EVENT, testEventResult);
//    }
}

package com.touristskaya.expenses.refactor_v2_3.utils.common.system_events;

import android.util.Log;

/**
 * TODO: Add a class header comment
 */

public class SystemEventsHandler {
    public static void onInfo(String textInfo) {
        Log.d("tag", textInfo);
    }

    public static void onError(String error) {
        Log.d("tag", "ERROR: " + error);
    }
}

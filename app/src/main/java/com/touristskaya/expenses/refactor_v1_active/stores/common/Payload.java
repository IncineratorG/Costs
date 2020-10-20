package com.touristskaya.expenses.refactor_v1_active.stores.common;

import java.util.HashMap;
import java.util.Map;

public class Payload {
    private static final String TAG = "tag";

    private Map<String, Object> mMap;


    public Payload() {
        mMap = new HashMap<>();
    }

    public void set(String key, Object value) {
        mMap.put(key, value);
    }

    public Object get(String key) {
        if (mMap.containsKey(key)) {
            return mMap.get(key);
        }

        return null;
    }
}

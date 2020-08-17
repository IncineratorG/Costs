package com.touristskaya.expenses.src.services;

import com.touristskaya.expenses.src.libs.service.Service;
import com.touristskaya.expenses.src.services.system.SystemService;
import com.touristskaya.expenses.unused.stores.realisation.Stores;

/**
 * TODO: Add a class header comment
 */

public class AppServices {
    public static final String SYSTEM_SERVICE = "SYSTEM_SERVICE";

    private static AppServices mInstance = null;

    private SystemService mSystemService;

    private AppServices() {
        mSystemService = new SystemService();
    }

    public static synchronized AppServices getInstance() {
        if (mInstance == null) {
            mInstance = new AppServices();
        }

        return mInstance;
    }

    public void init() {
        mSystemService.init();
    }

    public Service get(String serviceType) {
        switch (serviceType) {
            case SYSTEM_SERVICE: {
                return mSystemService;
            }

            default: {
                return null;
            }
        }
    }
}

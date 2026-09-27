package com.greencix.citygo;

import android.app.Application;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.network.SupabaseApiClient;

public class CityGoApp extends Application {

    private static CityGoApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Inicializar SessionManager y Cliente Supabase
        SessionManager.init(this);
        SupabaseApiClient.init(this);
    }

    public static CityGoApp getInstance() {
        return instance;
    }
}

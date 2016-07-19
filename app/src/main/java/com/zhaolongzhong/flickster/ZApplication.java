package com.zhaolongzhong.flickster;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ZApplication extends Application {
    private static final String TAG = ZApplication.class.getSimpleName();

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.getDefaultInstance();
    }

    public static Application getApplication() {
        return application;
    }
}

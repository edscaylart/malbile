package br.scaylart.malbile;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.utils.PreferenceUtils;
import io.fabric.sdk.android.Fabric;

public class MalbileApplication extends Application {
    private static MalbileApplication sInstance;
    Locale locale;
    Configuration config;

    public MalbileApplication() {
        sInstance = this;
    }

    public static synchronized MalbileApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        AccountService.create(getApplicationContext());

        //locale = PrefManager.getLocale();
        config = new Configuration();
        config.locale = locale;
        setLanguage(); //Change language when it is started

        initializePreferences();
    }

    public void setLanguage() {
        Resources res = getBaseContext().getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLanguage(); //Change language after orientation.
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Glide.get(this).trimMemory(level);
    }

    private void initializePreferences() {
        PreferenceUtils.initializePreferences();
    }
}

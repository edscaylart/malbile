package br.scaylart.malbile.views.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.TimeUnit;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.utils.PreferenceUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends ActionBarActivity {
    public static final String TAG = SplashActivity.class.getSimpleName();

    private Subscription mSplashSubscription;
    public boolean isLogged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashSubscription = Observable
                .create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        try {
                            isLogged = AccountService.getAccount() != null;
                            subscriber.onNext(isLogged);
                            subscriber.onCompleted();
                        } catch (Throwable e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .delay(1, TimeUnit.SECONDS, Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (isLogged) {
                            Intent startMain = new Intent(SplashActivity.this, MainActivity.class);
                            startMain.putExtra(MainActivity.POSITION_ARGUMENT_KEY, PreferenceUtils.getStartupScreen());
                            startActivity(startMain);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Intent startLogin = new Intent(SplashActivity.this, LoginActivity.class);
                            //startMain.putExtra(MainActivity.POSITION_ARGUMENT_KEY, PreferenceUtils.getStartupScreen());
                            startActivity(startLogin);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        // TODO do something
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSplashSubscription != null) {
            mSplashSubscription.unsubscribe();
            mSplashSubscription = null;
        }
    }
}

package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.events.DataQueryEvent;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.presenters.listeners.ProfilePresenter;
import br.scaylart.malbile.views.activities.ProfileActivity;
import br.scaylart.malbile.views.listeners.ProfileView;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProfilePresenterImpl implements ProfilePresenter{
    public static final String TAG = ProfilePresenterImpl.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    private String username;
    private ProfileView mProfileView;

    Subscription mQueryUserSubscription;

    public ProfilePresenterImpl(ProfileView profileView) {
        mProfileView = profileView;
    }

    @Override
    public void handleInitialArguments(Intent arguments) {
        if (arguments != null) {
            if (arguments.hasExtra(ProfileActivity.USERNAME_ARGUMENT_KEY)) {
                username = arguments.getStringExtra(ProfileActivity.USERNAME_ARGUMENT_KEY);

                arguments.removeExtra(ProfileActivity.USERNAME_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mProfileView.initializeToolbar();
        mProfileView.initializeEmptyRelativeLayout();

        queryProfileFromNetwork();
    }

    @Override
    public void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterForEvents() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void saveState(Bundle outState) {
        if (username != null) {
            outState.putString(USERNAME_ARGUMENT_KEY, username);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(USERNAME_ARGUMENT_KEY)) {
            username = savedState.getString(USERNAME_ARGUMENT_KEY);

            savedState.remove(USERNAME_ARGUMENT_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }
    }

    @Override
    public void onResume() {

    }

    public void onEventMainThread(DataQueryEvent event) {
        if (event != null) {
            queryProfileFromNetwork();
        }
    }

    private void queryProfileFromNetwork() {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.downloadUserDataFromNetwork(username, AccountService.getUsername().equals(username))
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return QueryManager.queryStoreUserPofile(user, true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                        mProfileView.initializeViews(username);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        mProfileView.hideEmptyRelativeLayout();
                        mProfileView.setTitle(username);
                        mProfileView.setUserRecord(user);
                        mProfileView.setMenu();
                    }
                });
    }
}

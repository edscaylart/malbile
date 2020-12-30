package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;

import org.apache.http.NameValuePair;

import java.util.List;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.presenters.listeners.MessagePresenter;
import br.scaylart.malbile.views.activities.SendMessageActivity;
import br.scaylart.malbile.views.listeners.MessageView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SendMessagePresenterImpl implements MessagePresenter {
    public static final String TAG = SendMessagePresenterImpl.class.getSimpleName();
    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    private MessageView mMessageView;

    private String username;
    boolean send = false;

    Subscription mQueryUserSubscription;

    public SendMessagePresenterImpl(MessageView messageView) {
        mMessageView = messageView;
    }

    @Override
    public void handleInitialArguments(Intent arguments) {
        if (arguments != null) {
            if (arguments.hasExtra(SendMessageActivity.USERNAME_ARGUMENT_KEY)) {
                username = arguments.getStringExtra(SendMessageActivity.USERNAME_ARGUMENT_KEY);

                arguments.removeExtra(SendMessageActivity.USERNAME_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mMessageView.initializeToolbar();
        mMessageView.initializeViews(username);
    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

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

    @Override
    public void sendMessage(List<NameValuePair> nameValuePairList) {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.postMessageToUser(username, nameValuePairList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (send) {
                            mMessageView.closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        send = aBoolean;
                        mMessageView.hideProgressDialog();
                    }
                });
    }

    @Override
    public void sendMessage(String subject, String msg) {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.postMessageToUser(username, subject, msg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (send) {
                            mMessageView.closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        send = aBoolean;
                        mMessageView.hideProgressDialog();
                    }
                });
    }
}

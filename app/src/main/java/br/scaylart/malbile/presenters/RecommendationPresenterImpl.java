package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;

import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.presenters.listeners.RecommendationPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.RecommendationActivity;
import br.scaylart.malbile.views.listeners.RecommendationView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RecommendationPresenterImpl implements RecommendationPresenter {
    public static final String TAG = RecommendationPresenterImpl.class.getSimpleName();
    private static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestParcelableKey";
    private static final String TITLE_ARGUMENT_KEY = TAG + ":" + "TitleParcelableKey";

    private RecommendationView mRecommendationView;

    private RequestWrapper mRequest;
    private String mTitle;

    boolean send = false;

    Subscription mQueryUserSubscription;

    public RecommendationPresenterImpl(RecommendationView recommendationView) {
        mRecommendationView = recommendationView;
    }

    @Override
    public void handleInitialArguments(Intent arguments) {
        if (arguments != null) {
            if (arguments.hasExtra(RecommendationActivity.REQUEST_ARGUMENT_KEY)) {
                mRequest = arguments.getParcelableExtra(RecommendationActivity.REQUEST_ARGUMENT_KEY);
                arguments.removeExtra(RecommendationActivity.REQUEST_ARGUMENT_KEY);
            }
            if (arguments.hasExtra(RecommendationActivity.TITLE_ARGUMENT_KEY)) {
                mTitle = arguments.getStringExtra(RecommendationActivity.TITLE_ARGUMENT_KEY);
                arguments.removeExtra(RecommendationActivity.TITLE_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mRecommendationView.initializeToolbar();
        mRecommendationView.initializeViews(mRequest, mTitle);
        mRecommendationView.setTitle(mTitle);
    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

    }

    @Override
    public void saveState(Bundle outState) {
        if (mRequest != null) {
            outState.putParcelable(REQUEST_ARGUMENT_KEY, mRequest);
            outState.putString(TITLE_ARGUMENT_KEY, mTitle);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(REQUEST_ARGUMENT_KEY)) {
            mRequest = savedState.getParcelable(REQUEST_ARGUMENT_KEY);
            savedState.remove(REQUEST_ARGUMENT_KEY);
        }
        if (savedState.containsKey(TITLE_ARGUMENT_KEY)) {
            mTitle = savedState.getString(TITLE_ARGUMENT_KEY);
            savedState.remove(TITLE_ARGUMENT_KEY);
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
    public void sendRecommendation(List<NameValuePair> nameValuePairList) {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.postRecommendation(mRequest, nameValuePairList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (send) {
                            mRecommendationView.closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        send = aBoolean;
                        mRecommendationView.hideProgressDialog();
                    }
                });
    }

    @Override
    public void sendRecommendation(HashMap<String, String> fieldMap) {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.postRecommendation(mRequest, fieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (send) {
                            mRecommendationView.closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        send = aBoolean;
                        mRecommendationView.hideProgressDialog();
                    }
                });
    }
}

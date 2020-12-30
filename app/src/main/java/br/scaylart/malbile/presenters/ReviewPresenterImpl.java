package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;

import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.presenters.listeners.ReviewPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.ReviewActivity;
import br.scaylart.malbile.views.listeners.ReviewView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewPresenterImpl implements ReviewPresenter {
    public static final String TAG = ReviewPresenterImpl.class.getSimpleName();
    private static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestParcelableKey";
    private static final String PROGRESS_ARGUMENT_KEY = TAG + ":" + "ProgressParcelableKey";
    private static final String TITLE_ARGUMENT_KEY = TAG + ":" + "TitleParcelableKey";

    private ReviewView mReviewView;

    private RequestWrapper mRequest;
    private int mProgress;
    private String mTitle;

    boolean send = false;

    Subscription mQueryUserSubscription;

    public ReviewPresenterImpl(ReviewView reviewView) {
        mReviewView = reviewView;
    }

    @Override
    public void handleInitialArguments(Intent arguments) {
        if (arguments != null) {
            if (arguments.hasExtra(ReviewActivity.REQUEST_ARGUMENT_KEY)) {
                mRequest = arguments.getParcelableExtra(ReviewActivity.REQUEST_ARGUMENT_KEY);
                arguments.removeExtra(ReviewActivity.REQUEST_ARGUMENT_KEY);
            }
            if (arguments.hasExtra(ReviewActivity.PROGRESS_ARGUMENT_KEY)) {
                mProgress = arguments.getIntExtra(ReviewActivity.PROGRESS_ARGUMENT_KEY, 0);
                arguments.removeExtra(ReviewActivity.PROGRESS_ARGUMENT_KEY);
            }
            if (arguments.hasExtra(ReviewActivity.TITLE_ARGUMENT_KEY)) {
                mTitle = arguments.getStringExtra(ReviewActivity.TITLE_ARGUMENT_KEY);
                arguments.removeExtra(ReviewActivity.TITLE_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mReviewView.initializeToolbar();
        mReviewView.initializeViews(mRequest, mProgress, mTitle);
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
            outState.putInt(PROGRESS_ARGUMENT_KEY, mProgress);
            outState.putString(TITLE_ARGUMENT_KEY, mTitle);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(REQUEST_ARGUMENT_KEY)) {
            mRequest = savedState.getParcelable(REQUEST_ARGUMENT_KEY);
            savedState.remove(REQUEST_ARGUMENT_KEY);
        }
        if (savedState.containsKey(PROGRESS_ARGUMENT_KEY)) {
            mProgress = savedState.getInt(PROGRESS_ARGUMENT_KEY);
            savedState.remove(PROGRESS_ARGUMENT_KEY);
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
    public void sendReview(List<NameValuePair> nameValuePairList) {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.postReview(mRequest, nameValuePairList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (send) {
                            mReviewView.closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        send = aBoolean;
                        mReviewView.hideProgressDialog();
                    }
                });
    }

    @Override
    public void sendReview(HashMap<String, String> fieldMap) {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager.postReview(mRequest, fieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (send) {
                            mReviewView.closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        send = false;
                        mReviewView.hideProgressDialog();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        send = aBoolean;
                        mReviewView.hideProgressDialog();
                    }
                });
    }
}

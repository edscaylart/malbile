package br.scaylart.malbile.presenters;

import android.os.Bundle;
import android.view.View;

import br.scaylart.malbile.presenters.listeners.DetailFragmentPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.fragments.DetailGeneralFragment;
import br.scaylart.malbile.views.listeners.DetailFragmentView;

public class DetailGeneralPresenterImpl implements DetailFragmentPresenter {
    public static final String TAG = DetailGeneralPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";

    private RequestWrapper mRequest;
    private DetailFragmentView mDetailFragmentView;

    public DetailGeneralPresenterImpl(DetailFragmentView detailFragmentView) {
        mDetailFragmentView = detailFragmentView;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(DetailGeneralFragment.REQUEST_ARGUMENT_KEY)) {
                mRequest = arguments.getParcelable(DetailGeneralFragment.REQUEST_ARGUMENT_KEY);
                arguments.remove(DetailGeneralFragment.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mDetailFragmentView.initializeViews();
    }

    @Override
    public void initializeData() {
        mDetailFragmentView.initializeData(mRequest);
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
            outState.putParcelable(REQUEST_PARCELABLE_KEY, mRequest);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(REQUEST_PARCELABLE_KEY)) {
            mRequest = savedState.getParcelable(REQUEST_PARCELABLE_KEY);

            savedState.remove(REQUEST_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {

    }

    @Override
    public void releaseAllResources() {

    }

    @Override
    public void onClick(View v) {

    }
}

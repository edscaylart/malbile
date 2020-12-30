package br.scaylart.malbile.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.presenters.DetailUserRecsPresenterImpl;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.activities.RecommendationActivity;
import butterknife.OnClick;


public class DetailUserRecsFragment extends BaseListFragment {
    public static final String TAG = DetailReviewFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    public static DetailUserRecsFragment newInstance(RequestWrapper mRequest) {
        DetailUserRecsFragment newInstance = new DetailUserRecsFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(REQUEST_ARGUMENT_KEY, mRequest);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mLibraryPresenter = new DetailUserRecsPresenterImpl(this, this);
        }
    }

    @Override
    protected void setFloatButtonVisibility() {
        mFloatingBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public DetailActivity getContext() {
        return (DetailActivity) getActivity();
    }

    @OnClick(R.id.listEdit)
    public void onReview(View v) {
        String title;

        if (mLibraryPresenter.getRequestWrapper().getListType().equals(BaseService.ListType.ANIME)) {
            title = getContext().animeRecord.getTitle();
        } else {
            title = getContext().mangaRecord.getTitle();
        }
        Intent msgIntent = RecommendationActivity.constructRecommendationActivityIntent(getContext(), mLibraryPresenter.getRequestWrapper(), title);
        getContext().startActivity(msgIntent);
    }
}
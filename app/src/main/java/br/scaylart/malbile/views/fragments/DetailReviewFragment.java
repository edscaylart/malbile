package br.scaylart.malbile.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.presenters.DetailReviewPresenterImpl;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.activities.ReviewActivity;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DetailReviewFragment extends BaseListFragment {
    public static final String TAG = DetailReviewFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    public static DetailReviewFragment newInstance(RequestWrapper mRequest) {
        DetailReviewFragment newInstance = new DetailReviewFragment();

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
            mLibraryPresenter = new DetailReviewPresenterImpl(this, this);
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
        int progress;
        String title;

        if (mLibraryPresenter.getRequestWrapper().getListType().equals(BaseService.ListType.ANIME)) {
            progress = getContext().animeRecord.getWatchedEpisodes();
            title = getContext().animeRecord.getTitle();
        } else {
            progress = getContext().mangaRecord.getChaptersRead();
            title = getContext().mangaRecord.getTitle();
        }
        Intent msgIntent = ReviewActivity.constructReviewActivityIntent(getContext(), mLibraryPresenter.getRequestWrapper(), progress, title);
        getContext().startActivity(msgIntent);
    }
}

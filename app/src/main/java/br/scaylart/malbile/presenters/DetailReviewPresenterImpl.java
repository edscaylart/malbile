package br.scaylart.malbile.presenters;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.Review;
import br.scaylart.malbile.presenters.listeners.LibraryPresenter;
import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.adapters.ReviewAdapter;
import br.scaylart.malbile.views.fragments.DetailReviewFragment;
import br.scaylart.malbile.views.listeners.LibraryView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailReviewPresenterImpl implements LibraryPresenter {
    public static final String TAG = DetailReviewPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";
    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    protected RequestWrapper mRequest;

    protected LibraryView mLibraryView;
    protected LibraryMapper mLibraryMapper;
    protected ReviewAdapter mLibraryAdapter;

    protected Parcelable mPositionSavedState;

    protected Subscription mQueryReviewSubscription;

    private int page = 1;

    public DetailReviewPresenterImpl(LibraryView libraryView, LibraryMapper libraryMapper) {
        mLibraryView = libraryView;
        mLibraryMapper = libraryMapper;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(DetailReviewFragment.REQUEST_ARGUMENT_KEY)) {
                mRequest = arguments.getParcelable(DetailReviewFragment.REQUEST_ARGUMENT_KEY);
                arguments.remove(DetailReviewFragment.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mLibraryView.initializeAbsListView();
        mLibraryView.initializeEmptyRelativeLayout();
    }

    @Override
    public void initializeSearch() {

    }

    @Override
    public void initializeData() {
        if (mLibraryAdapter == null) {
            mLibraryAdapter = new ReviewAdapter(mLibraryView.getContext(), mRequest.getListType());
            mLibraryMapper.registerAdapter(mLibraryAdapter);

            queryReviewFromNetwork();
        }
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
        if (mLibraryMapper.getPositionState() != null) {
            outState.putParcelable(POSITION_PARCELABLE_KEY, mLibraryMapper.getPositionState());
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(REQUEST_PARCELABLE_KEY)) {
            mRequest = savedState.getParcelable(REQUEST_PARCELABLE_KEY);
            savedState.remove(REQUEST_PARCELABLE_KEY);
        }
        if (savedState.containsKey(POSITION_PARCELABLE_KEY)) {
            mPositionSavedState = savedState.getParcelable(POSITION_PARCELABLE_KEY);
            savedState.remove(POSITION_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryReviewSubscription != null) {
            mQueryReviewSubscription.unsubscribe();
            mQueryReviewSubscription = null;
        }
    }

    @Override
    public void releaseAllResources() {
        if (mLibraryAdapter != null) {
            mLibraryAdapter.setRecords(null);
            mLibraryAdapter = null;
        }
    }

    @Override
    public void onRecordClick(int position) {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void searchFromNetwork(String query) {

    }

    @Override
    public void onOptionFilter() {

    }

    @Override
    public void onOptionToTop() {

    }

    @Override
    public RequestWrapper getRequestWrapper() {
        return mRequest;
    }

    public void queryReviewFromNetwork() {
        if (mQueryReviewSubscription != null) {
            mQueryReviewSubscription.unsubscribe();
            mQueryReviewSubscription = null;
        }

        Anime anime = ((DetailActivity) mLibraryView.getContext()).animeRecord;
        Manga manga = ((DetailActivity) mLibraryView.getContext()).mangaRecord;

        String title = isAnime() ? anime.getTitle() : manga.getTitle();
        String id = isAnime() ? Integer.toString(anime.getId()) : Integer.toString(manga.getId());
        String type = isAnime() ? "anime" : "manga";

        mQueryReviewSubscription = MalbileManager
                .downloadReviewFromNetwork(type, id, title.replace(" ", "_"), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Review>>() {
                    @Override
                    public void onCompleted() {
                        restorePosition();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(ArrayList<Review> reviews) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(reviews);
                        }

                        if (reviews != null && reviews.size() > 0) {
                            mLibraryView.hideEmptyRelativeLayout();
                        } else {
                            mLibraryView.showEmptyRelativeLayout();
                        }
                    }
                });
    }

    private void restorePosition() {
        if (mPositionSavedState != null) {
            mLibraryMapper.setPositionState(mPositionSavedState);
            mPositionSavedState = null;
        }
    }

    private boolean isAnime() {
        return mRequest.getListType().equals(BaseService.ListType.ANIME);
    }

}

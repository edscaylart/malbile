package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.events.SearchCatalogueWrapperSubmitEvent;
import br.scaylart.malbile.controllers.factories.DefaultFactory;
import br.scaylart.malbile.presenters.listeners.CataloguePresenter;
import br.scaylart.malbile.presenters.mapper.CatalogueMapper;
import br.scaylart.malbile.reader.English_MangaEden;
import br.scaylart.malbile.reader.English_MangaReader;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.utils.SearchUtils;
import br.scaylart.malbile.utils.wrappers.ReaderWrapper;
import br.scaylart.malbile.utils.wrappers.SearchCatalogueWrapper;
import br.scaylart.malbile.views.activities.MangaActivity;
import br.scaylart.malbile.views.adapters.CatalogueAdapter;
import br.scaylart.malbile.views.listeners.CatalogueView;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class CataloguePresenterImpl implements CataloguePresenter {
    public static final String TAG = CataloguePresenterImpl.class.getSimpleName();

    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    private CatalogueView mCatalogueView;
    private CatalogueMapper mCatalogueMapper;
    private CatalogueAdapter mCatalogueAdapter;

    private SearchCatalogueWrapper mSearchCatalogueWrapper;

    private Parcelable mPositionSavedState;

    private Subscription mQueryCatalogueMangaSubscription;
    private Subscription mDownloadCatalogueMangaSubscription;
    private Subscription mSearchViewSubscription;
    private PublishSubject<Observable<String>> mSearchViewPublishSubject;

    public static boolean downloadedData = false;

    public CataloguePresenterImpl(CatalogueView catalogueView, CatalogueMapper catalogueMapper) {
        mCatalogueView = catalogueView;
        mCatalogueMapper = catalogueMapper;

        mSearchCatalogueWrapper = DefaultFactory.SearchCatalogueWrapper.constructDefault();
    }

    @Override
    public void initializeViews() {
        mCatalogueView.initializeToolbar();
        mCatalogueView.initializeAbsListView();
        mCatalogueView.initializeEmptyRelativeLayout();
        mCatalogueView.initializeButtons();
    }

    @Override
    public void initializeSearch() {
        mSearchViewPublishSubject = PublishSubject.create();
        mSearchViewSubscription = Observable.switchOnNext(mSearchViewPublishSubject)
                .debounce(SearchUtils.TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        queryCatalogueMangaFromPreferenceSource();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(String query) {
                        if (mSearchCatalogueWrapper != null) {
                            mSearchCatalogueWrapper.setNameArgs(query);
                            mSearchCatalogueWrapper.setOffsetArgs(DefaultFactory.SearchCatalogueWrapper.DEFAULT_OFFSET);
                        }

                        onCompleted();
                    }
                });
    }

    @Override
    public void initializeDataFromPreferenceSource() {
        mCatalogueAdapter = new CatalogueAdapter(mCatalogueView.getContext());

        mCatalogueMapper.registerAdapter(mCatalogueAdapter);

        queryCatalogueMangaFromPreferenceSource();
    }

    @Override
    public void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(SearchCatalogueWrapperSubmitEvent event) {
        if (event != null && event.getSearchCatalogueWrapper() != null) {
            mSearchCatalogueWrapper = event.getSearchCatalogueWrapper();

            queryCatalogueMangaFromPreferenceSource();
        }
    }

    @Override
    public void unregisterForEvents() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void saveState(Bundle outState) {
        if (mSearchCatalogueWrapper != null) {
            outState.putParcelable(SearchCatalogueWrapper.PARCELABLE_KEY, mSearchCatalogueWrapper);
        }
        if (mCatalogueMapper.getPositionState() != null) {
            outState.putParcelable(POSITION_PARCELABLE_KEY, mCatalogueMapper.getPositionState());
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(SearchCatalogueWrapper.PARCELABLE_KEY)) {
            mSearchCatalogueWrapper = savedState.getParcelable(SearchCatalogueWrapper.PARCELABLE_KEY);

            savedState.remove(SearchCatalogueWrapper.PARCELABLE_KEY);
        }
        if (savedState.containsKey(POSITION_PARCELABLE_KEY)) {
            mPositionSavedState = savedState.getParcelable(POSITION_PARCELABLE_KEY);

            savedState.remove(POSITION_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryCatalogueMangaSubscription != null) {
            mQueryCatalogueMangaSubscription.unsubscribe();
            mQueryCatalogueMangaSubscription = null;
        }
        if (mSearchViewSubscription != null) {
            mSearchViewSubscription.unsubscribe();
            mSearchViewSubscription = null;
        }
    }

    @Override
    public void releaseAllResources() {
        if (mCatalogueAdapter != null) {
            mCatalogueAdapter.setRecords(null);
            mCatalogueAdapter = null;
        }
    }

    @Override
    public void onMangaClick(int position) {
        if (mCatalogueAdapter != null) {
            MangaEden selectedManga = mCatalogueAdapter.getDataByIndex(position);
            if (selectedManga != null) {
                String mangaUrl = selectedManga.getUrl();

                Intent mangaIntent = MangaActivity.constructMangaActivityIntent(mCatalogueView.getContext(), new ReaderWrapper("MangaEden (EN)", mangaUrl));
                mCatalogueView.getContext().startActivity(mangaIntent);
            }
        }
    }

    @Override
    public void onQueryTextChange(String query) {
        if (mSearchViewPublishSubject != null) {
            mSearchViewPublishSubject.onNext(Observable.just(query));
        }
    }

    @Override
    public void onPreviousClick() {
        if (mSearchCatalogueWrapper != null) {
            int currentOffset = mSearchCatalogueWrapper.getOffsetArgs();
            if (currentOffset - SearchUtils.LIMIT_COUNT >= 0) {
                mSearchCatalogueWrapper.setOffsetArgs(currentOffset - SearchUtils.LIMIT_COUNT);

                queryCatalogueMangaFromPreferenceSource();

                return;
            }
        }

        mCatalogueView.toastNoPreviousPage();
    }

    @Override
    public void onNextClick() {
        if (mSearchCatalogueWrapper != null) {
            if (mCatalogueAdapter != null) {
                if (mCatalogueAdapter.getItemCount() == SearchUtils.LIMIT_COUNT) {
                    int currentOffset = mSearchCatalogueWrapper.getOffsetArgs();

                    mSearchCatalogueWrapper.setOffsetArgs(currentOffset + SearchUtils.LIMIT_COUNT);

                    queryCatalogueMangaFromPreferenceSource();

                    return;
                }
            }
        }

        mCatalogueView.toastNoNextPage();
    }

    @Override
    public void onOptionFilter() {

    }

    @Override
    public void onOptionToTop() {
        mCatalogueView.scrollToTop();
    }

    private void queryCatalogueMangaFromPreferenceSource() {
        if (mQueryCatalogueMangaSubscription != null) {
            mQueryCatalogueMangaSubscription.unsubscribe();
            mQueryCatalogueMangaSubscription = null;
        }

        if (mSearchCatalogueWrapper != null) {

            mQueryCatalogueMangaSubscription = QueryManager
                    .queryCatalogueMangasFromPreferenceSource(mSearchCatalogueWrapper)
                    .flatMap(new Func1<ArrayList<MangaEden>, Observable<ArrayList<MangaEden>>>() {
                        @Override
                        public Observable<ArrayList<MangaEden>> call(ArrayList<MangaEden> mangaEdens) {
                            return Observable.just(mangaEdens);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<MangaEden>>() {
                        @Override
                        public void onCompleted() {
                            restorePosition();
                            if (!downloadedData) {
                                downloadCatalogueMangaFromPreferenceSource();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNext(ArrayList<MangaEden> mangas) {
                            if (mCatalogueAdapter != null) {
                                mCatalogueAdapter.setRecords(mangas);
                            }

                            if (mangas != null) {
                                mCatalogueView.hideEmptyRelativeLayout();
                            } else {
                                mCatalogueView.showEmptyRelativeLayout();
                            }

                            mCatalogueView.setSubtitlePositionText(getPageNumber());
                        }
                    });
        }
    }

    private void downloadCatalogueMangaFromPreferenceSource() {
        if (mDownloadCatalogueMangaSubscription != null) {
            mDownloadCatalogueMangaSubscription.unsubscribe();
            mDownloadCatalogueMangaSubscription = null;
        }

        mDownloadCatalogueMangaSubscription = MalbileManager
                .recursivelyConstructDatabase(new ReaderWrapper(MalbileManager.getNameFromPreferenceSource().toBlocking().single(), ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               /* .takeWhile(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s != null;
                    }
                }) */
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        downloadedData = true;
                        queryCatalogueMangaFromPreferenceSource();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                    }
                });
    }

    private void restorePosition() {
        if (mPositionSavedState != null) {
            mCatalogueMapper.setPositionState(mPositionSavedState);

            mPositionSavedState = null;
        }
    }

    private int getPageNumber() {
        return (mSearchCatalogueWrapper != null) ? (mSearchCatalogueWrapper.getOffsetArgs() + SearchUtils.LIMIT_COUNT) / SearchUtils.LIMIT_COUNT : 0;
    }
}

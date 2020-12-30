package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.AnimeList;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.MangaList;
import br.scaylart.malbile.presenters.listeners.LibraryPresenter;
import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.adapters.LibraryAdapter;
import br.scaylart.malbile.views.listeners.LibraryView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BaseLibraryPresenterImpl implements LibraryPresenter {

    protected LibraryWrapper mLibraryWrapper;

    protected LibraryView mLibraryView;
    protected LibraryMapper mLibraryMapper;
    protected LibraryAdapter mLibraryAdapter;

    protected Parcelable mPositionSavedState;

    protected Subscription mQueryLibrarySubscription;

    int page = 0;

    public void handleInitialArguments(Bundle arguments) {

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
            mLibraryAdapter = new LibraryAdapter(mLibraryView.getContext(), mLibraryWrapper.getListType(), mLibraryWrapper.getTaskJob());
            mLibraryMapper.registerAdapter(mLibraryAdapter);

            if (mLibraryWrapper.getListType().equals(BaseService.ListType.ANIME)) {
                if (mLibraryWrapper.getTaskJob().equals(MalbileManager.TaskJob.LIBRARY))
                    queryAnimeLibraryFromDataBase();
                else if (!mLibraryWrapper.getTaskJob().equals(MalbileManager.TaskJob.SEARCH))
                    queryAnimeOfTaskjob();
            } else {
                if (mLibraryWrapper.getTaskJob().equals(MalbileManager.TaskJob.LIBRARY))
                    queryMangaLibraryFromDataBase();
                else if (!mLibraryWrapper.getTaskJob().equals(MalbileManager.TaskJob.SEARCH))
                    queryMangaOfTaskjob();
            }
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
        if (mLibraryWrapper != null) {
            outState.putParcelable(getRequestParcelableKey(), mLibraryWrapper);
        }
        if (mLibraryMapper.getPositionState() != null) {
            outState.putParcelable(getPositionParcelableKey(), mLibraryMapper.getPositionState());
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(getRequestParcelableKey())) {
            mLibraryWrapper = savedState.getParcelable(getRequestParcelableKey());
            savedState.remove(getRequestParcelableKey());
        }
        if (savedState.containsKey(getPositionParcelableKey())) {
            mPositionSavedState = savedState.getParcelable(getPositionParcelableKey());
            savedState.remove(getPositionParcelableKey());
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
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
        if (mLibraryAdapter != null) {
            BaseRecord record;
            if (mLibraryWrapper.getListType().equals(BaseService.ListType.ANIME)) {
                record = mLibraryAdapter.getDataByIndex(position);
            } else {
                record = mLibraryAdapter.getDataByIndex(position);
            }
            if (record != null) {
                BaseService.ListType listType = mLibraryWrapper.getListType();
                String username = mLibraryWrapper.getUsername();
                Intent detailIntent = DetailActivity.constructDetailActivityIntent(mLibraryView.getContext(), new RequestWrapper(listType, record.getId(), username));
                mLibraryView.getContext().startActivity(detailIntent);
            }
        }
    }

    @Override
    public void refreshData() {
        if (mLibraryAdapter != null) {
            if (mLibraryWrapper.getListType().equals(BaseService.ListType.ANIME))
                queryAnimeLibraryFromDataBase();
            else
                queryMangaLibraryFromDataBase();
        } else {
            initializeData();
        }
    }

    @Override
    public void searchFromNetwork(String query) {
        if (mLibraryAdapter != null) {
            if (mLibraryWrapper.getListType().equals(BaseService.ListType.ANIME))
                querySearchAnime(query);
            else
                querySearchManga(query);
        } else {
            initializeData();
        }
    }

    @Override
    public void onOptionFilter() {

    }

    @Override
    public void onOptionToTop() {

    }

    @Override
    public RequestWrapper getRequestWrapper() {
        return null;
    }

    public String getRequestParcelableKey() {
        return null;
    }

    public String getPositionParcelableKey() {
        return null;
    }

    public void queryMangaLibraryFromDataBase() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = QueryManager
                .queryMangaLibraryFromDataBase(mLibraryWrapper, mLibraryWrapper.getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MangaList>() {
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
                    public void onNext(MangaList mangaList) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(mangaList.getMangas());
                        }

                        if (mangaList != null && mangaList.getMangas() != null) {
                            mLibraryView.hideEmptyRelativeLayout();
                        } else {
                            mLibraryView.showEmptyRelativeLayout();
                        }
                    }
                });
    }

    public void queryAnimeLibraryFromDataBase() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = QueryManager
                .queryAnimeLibraryFromDataBase(mLibraryWrapper, mLibraryWrapper.getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnimeList>() {
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
                    public void onNext(AnimeList animeList) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(animeList.getAnimes());
                        }

                        if (animeList != null && animeList.getAnimes() != null) {
                            mLibraryView.hideEmptyRelativeLayout();
                        } else {
                            mLibraryView.showEmptyRelativeLayout();
                        }
                    }
                });
    }

    public void queryAnimeOfTaskjob() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = MalbileManager
                .downloadAnimeListOfTaskjob(mLibraryWrapper.getTaskJob(), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Anime>>() {
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
                    public void onNext(ArrayList<Anime> animes) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(animes);
                        }

                        if (animes != null && animes.size() > 0) {
                            mLibraryView.hideEmptyRelativeLayout();
                        } else {
                            mLibraryView.showEmptyRelativeLayout();
                        }
                    }
                });
    }

    public void queryMangaOfTaskjob() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = MalbileManager
                .downloadMangaListOfTaskjob(mLibraryWrapper.getTaskJob(), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Manga>>() {
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
                    public void onNext(ArrayList<Manga> mangas) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(mangas);
                        }

                        if (mangas != null && mangas.size() > 0) {
                            mLibraryView.hideEmptyRelativeLayout();
                        } else {
                            mLibraryView.showEmptyRelativeLayout();
                        }
                    }
                });
    }

    public void querySearchAnime(final String query) {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = MalbileManager
                .searchAnimeFromNetwork(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Anime>>() {
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
                    public void onNext(ArrayList<Anime> animes) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(animes);
                        }

                        if (animes != null && animes.size() > 0) {
                            mLibraryView.hideEmptyRelativeLayout();
                        } else {
                            mLibraryView.showEmptyRelativeLayout();
                        }
                    }
                });
    }

    public void querySearchManga(final String query) {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = MalbileManager
                .searchMangaFromNetwork(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Manga>>() {
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
                    public void onNext(ArrayList<Manga> mangas) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(mangas);
                        }

                        if (mangas != null && mangas.size() > 0) {
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
}

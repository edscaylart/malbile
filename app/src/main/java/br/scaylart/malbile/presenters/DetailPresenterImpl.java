package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.events.DataQueryEvent;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.presenters.listeners.DetailPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.listeners.DetailView;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DetailPresenterImpl implements DetailPresenter {
    public static final String TAG = DetailPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";

    private RequestWrapper mRequest;
    private DetailView mDetailView;

    private Subscription mQueryDataSubscription;
    private Subscription mQueryUpdateSubscription;
    private Subscription mQueryDeleteSubscription;

    public DetailPresenterImpl(DetailView detailView) {
        mDetailView = detailView;
    }

    @Override
    public void handleInitialArguments(Intent arguments) {
        if (arguments != null) {
            if (arguments.hasExtra(DetailActivity.REQUEST_ARGUMENT_KEY)) {
                mRequest = arguments.getParcelableExtra(DetailActivity.REQUEST_ARGUMENT_KEY);

                arguments.removeExtra(DetailActivity.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mDetailView.initializeToolbar();
        mDetailView.initializeEmptyRelativeLayout();

        if (mRequest.getListType().equals(BaseService.ListType.ANIME))
            queryAnimeData();
        else
            queryMangaData();
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
        if (mQueryDataSubscription != null) {
            mQueryDataSubscription.unsubscribe();
            mQueryDataSubscription = null;
        }
        if (mQueryUpdateSubscription != null) {
            mQueryUpdateSubscription.unsubscribe();
            mQueryUpdateSubscription = null;
        }
        if (mQueryDeleteSubscription != null) {
            mQueryDeleteSubscription.unsubscribe();
            mQueryDeleteSubscription = null;
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void updateInformations() {
        if (mRequest.getListType().equals(BaseService.ListType.ANIME))
            queryUpdateAnimeInformations();
        else
            queryUpdateMangaInformations();
    }

    @Override
    public void deleteInformations() {
        if (mRequest.getListType().equals(BaseService.ListType.ANIME))
            queryDeleteAnimeInformations();
        else
            queryDeleteMangaInformations();
    }

    @Override
    public RequestWrapper getRequestWrapper() {
        return mRequest;
    }

    public void onEventMainThread(DataQueryEvent event) {
        if (event != null) {
            if (mRequest.getListType().equals(BaseService.ListType.ANIME))
                queryAnimeData();
            else
                queryMangaData();
        }
    }

    private void queryAnimeData() {
        if (mQueryDataSubscription != null) {
            mQueryDataSubscription.unsubscribe();
            mQueryDataSubscription = null;
        }

        mQueryDataSubscription = QueryManager
                .queryGetAnime(mRequest.getId(), mRequest.getUsername())
                .flatMap(new Func1<Anime, Observable<Anime>>() {
                    @Override
                    public Observable<Anime> call(Anime anime) {
                        if (anime != null && anime.getSynopsis() != null) {
                            return Observable.just(anime);
                        } else {
                            return MalbileManager.downloadAnimeFromNetwork(mRequest.getId(), anime, mRequest.getUsername());
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Anime>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Anime anime) {
                        if (anime != null) {
                            mDetailView.hideEmptyRelativeLayout();
                            mDetailView.setAnimeRecord(anime);
                            mDetailView.setTitle(anime.getTitle());
                            mDetailView.initializeViews(mRequest);
                            mDetailView.setMenu(mRequest);
                        }
                    }
                });
    }

    private void queryMangaData() {
        if (mQueryDataSubscription != null) {
            mQueryDataSubscription.unsubscribe();
            mQueryDataSubscription = null;
        }

        mQueryDataSubscription = QueryManager
                .queryGetManga(mRequest.getId(), mRequest.getUsername())
                .flatMap(new Func1<Manga, Observable<Manga>>() {
                    @Override
                    public Observable<Manga> call(Manga manga) {
                        if (manga != null && manga.getSynopsis() != null) {
                            return Observable.just(manga);
                        } else {
                            return MalbileManager.downloadMangaFromNetwork(mRequest.getId(), manga, mRequest.getUsername());
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Manga>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Manga manga) {
                        if (manga != null) {
                            mDetailView.hideEmptyRelativeLayout();
                            mDetailView.setMangaRecord(manga);
                            mDetailView.setTitle(manga.getTitle());
                            mDetailView.initializeViews(mRequest);
                            mDetailView.setMenu(mRequest);
                        }
                    }
                });
    }

    private void queryUpdateAnimeInformations() {
        if (mQueryUpdateSubscription != null) {
            mQueryUpdateSubscription.unsubscribe();
            mQueryUpdateSubscription = null;
        }

        mQueryUpdateSubscription = MalbileManager
                .addOrUpdateAnime(mDetailView.getAnimeRecord())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mDetailView.onUpdatedInformation();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDetailView.hideProgressDialog();
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mDetailView.hideProgressDialog();
                    }
                });
    }

    private void queryUpdateMangaInformations() {
        if (mQueryUpdateSubscription != null) {
            mQueryUpdateSubscription.unsubscribe();
            mQueryUpdateSubscription = null;
        }

        mQueryUpdateSubscription = MalbileManager
                .addOrUpdateManga(mDetailView.getMangaRecord())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mDetailView.onUpdatedInformation();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDetailView.hideProgressDialog();
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mDetailView.hideProgressDialog();
                    }
                });
    }

    private void queryDeleteAnimeInformations() {
        if (mQueryDeleteSubscription != null) {
            mQueryDeleteSubscription.unsubscribe();
            mQueryDeleteSubscription = null;
        }

        mQueryDeleteSubscription = MalbileManager
                .deleteAnimeFromList(mDetailView.getAnimeRecord())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mDetailView.onUpdatedInformation();
                        mDetailView.closeActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDetailView.hideProgressDialog();
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mDetailView.hideProgressDialog();
                    }
                });
    }

    private void queryDeleteMangaInformations() {
        if (mQueryDeleteSubscription != null) {
            mQueryDeleteSubscription.unsubscribe();
            mQueryDeleteSubscription = null;
        }

        mQueryDeleteSubscription = MalbileManager
                .deleteMangaFromList(mDetailView.getMangaRecord())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mDetailView.onUpdatedInformation();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDetailView.hideProgressDialog();
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mDetailView.hideProgressDialog();
                    }
                });
    }
}

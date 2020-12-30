package br.scaylart.malbile.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.models.AnimeList;
import br.scaylart.malbile.models.MangaList;
import br.scaylart.malbile.views.adapters.pager.LibraryPagerAdapter;
import br.scaylart.malbile.views.widget.SlidingTabLayout;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LibraryFragment extends Fragment {
    public static final String TAG = LibraryFragment.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";
    public static final String LISTTYPE_ARGUMENT_KEY = TAG + ":" + "ListTypeArgumentKey";

    public ListType listType = ListType.ANIME;
    public String username;

    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;

    LibraryPagerAdapter mLibraryPagerAdapter;

    Context context;

    Subscription mQueryLibrarySubscription;

    public LibraryFragment() {
        // Required empty public constructor
    }

    public static LibraryFragment newInstance(ListType listType, String username) {
        LibraryFragment newInstance = new LibraryFragment();

        Bundle arguments = new Bundle();
        arguments.putString(LISTTYPE_ARGUMENT_KEY, listType.name());
        arguments.putString(USERNAME_ARGUMENT_KEY, username);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            handleInitialArguments(getArguments());
        }

        initializeToolbar();

        if (listType.equals(ListType.ANIME))
            queryAnimeLibraryFromNetwork();
        else
            queryMangaLibraryFromNetwork();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLibraryPagerAdapter = null;
    }

    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(LISTTYPE_ARGUMENT_KEY)) {
                listType = ListType.valueOf(arguments.getString(LISTTYPE_ARGUMENT_KEY));
                arguments.remove(LISTTYPE_ARGUMENT_KEY);
            }
            if (arguments.containsKey(USERNAME_ARGUMENT_KEY)) {
                username = arguments.getString(USERNAME_ARGUMENT_KEY);
                arguments.remove(USERNAME_ARGUMENT_KEY);
            }
        }
    }

    public void initializeViews() {
        mLibraryPagerAdapter = new LibraryPagerAdapter(getFragmentManager(), listType, username);

        mViewPager.setAdapter(mLibraryPagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentPinkA200);
            }
        });
        mTabs.setViewPager(mViewPager);
    }

    public void initializeToolbar() {
        if (getActivity() instanceof ActionBarActivity) {
            String title = getResources().getString(listType.equals(ListType.ANIME) ? R.string.fragment_library_anime : R.string.fragment_library_manga);
            title = title.replace("$user", username);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(title);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(null);
        }
    }

    public void queryAnimeLibraryFromNetwork() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = MalbileManager
                .getAnimeLibrary(username)
                .flatMap(new Func1<AnimeList, Observable<AnimeList>>() {
                    @Override
                    public Observable<AnimeList> call(AnimeList animeList) {
                        return QueryManager.queryStoreAnimeLibrary(animeList, username);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnimeList>() {
                    @Override
                    public void onCompleted() {
                        initializeViews();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(AnimeList animeList) {
                    }
                });
    }

    public void queryMangaLibraryFromNetwork() {
        if (mQueryLibrarySubscription != null) {
            mQueryLibrarySubscription.unsubscribe();
            mQueryLibrarySubscription = null;
        }

        mQueryLibrarySubscription = MalbileManager
                .getMangaLibrary(username)
                .flatMap(new Func1<MangaList, Observable<MangaList>>() {
                    @Override
                    public Observable<MangaList> call(MangaList mangaList) {
                        return QueryManager.queryStoreMangaLibrary(mangaList, username);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MangaList>() {
                    @Override
                    public void onCompleted() {
                        initializeViews();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(MangaList mangaList) {
                    }
                });
    }

    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(LISTTYPE_ARGUMENT_KEY)) {
            listType = ListType.valueOf(savedState.getString(LISTTYPE_ARGUMENT_KEY));
            savedState.remove(LISTTYPE_ARGUMENT_KEY);
        }
        if (savedState.containsKey(USERNAME_ARGUMENT_KEY)) {
            username = savedState.getString(USERNAME_ARGUMENT_KEY);
            savedState.remove(USERNAME_ARGUMENT_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (listType != null) {
            outState.putString(LISTTYPE_ARGUMENT_KEY, listType.name());
        }
        if (username != null) {
            outState.putString(USERNAME_ARGUMENT_KEY, username);
        }
    }
}

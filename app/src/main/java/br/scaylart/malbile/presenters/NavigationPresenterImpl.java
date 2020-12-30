package br.scaylart.malbile.presenters;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.events.NavigationItemSelectEvent;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.presenters.listeners.NavigationPresenter;
import br.scaylart.malbile.presenters.mapper.NavigationMapper;
import br.scaylart.malbile.utils.NavigationUtils;
import br.scaylart.malbile.views.NavDrawerItem;
import br.scaylart.malbile.views.NavMenuItem;
import br.scaylart.malbile.views.NavMenuSection;
import br.scaylart.malbile.views.adapters.NavigationAdapter;
import br.scaylart.malbile.views.fragments.NavigationFragment;
import br.scaylart.malbile.views.listeners.NavigationView;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NavigationPresenterImpl implements NavigationPresenter {
    public static final String TAG = NavigationPresenterImpl.class.getSimpleName();

    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    private NavigationView mNavigationView;
    private NavigationMapper mNavigationMapper;

    private NavigationAdapter mNavigationAdapter;
    private int mCurrentPosition;

    private List<NavDrawerItem> navigationItems;

    public NavigationPresenterImpl(NavigationView navigationView, NavigationMapper navigationMapper) {
        mNavigationView = navigationView;
        mNavigationMapper = navigationMapper;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(NavigationFragment.POSITION_ARGUMENT_KEY)) {
                mCurrentPosition = arguments.getInt(NavigationFragment.POSITION_ARGUMENT_KEY);

                arguments.remove(NavigationFragment.POSITION_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mNavigationView.initializeAbsListView();
        mNavigationView.initializeUsernameTextView(AccountService.getUsername());

        downloadProfileDataFromNetwork();
    }

    @Override
    public void initializeNavigationFromResources() {
        if (navigationItems == null) {
            navigationItems = new ArrayList<>();
            //navigationItems.add(NavMenuSection.create(NavigationUtils.HEADER_PROFILE, R.string.header_profile_title));
            navigationItems.add(NavMenuSection.create(NavigationUtils.SECTION_LIBRARY, R.string.section_library_title));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_ANIME, R.string.navigation_anime_title, R.drawable.ic_anime));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_MANGA, R.string.navigation_manga_title, R.drawable.ic_manga));
            navigationItems.add(NavMenuSection.create(NavigationUtils.SECTION_PERSONAL, R.string.section_personal_title));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_FRIENDS, R.string.navigation_friends_title, R.drawable.ic_friends));
            navigationItems.add(NavMenuSection.create(NavigationUtils.SECTION_READER, R.string.section_reader_title));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_CATALOGUE, R.string.navigation_catalogue_title, R.drawable.ic_manga));
            //navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_READING_MANGAS, R.string.navigation_reading_manga_title, R.drawable.ic_manga));
            navigationItems.add(NavMenuSection.create(NavigationUtils.SECTION_MAL, R.string.section_mal_title));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_SEARCH, R.string.navigation_search, R.drawable.ic_search));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_JUST_ADDED, R.string.navigation_just_added_title, R.drawable.ic_time));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_MOST_POPULAR, R.string.navigation_most_popular_title, R.drawable.ic_most_popular));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_TOP_RATED, R.string.navigation_top_rated_title, R.drawable.ic_rated));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_UPCOMING, R.string.navigation_upcoming_title, R.drawable.ic_upcoming));
            navigationItems.add(NavMenuSection.create(NavigationUtils.SECTION_OTHER, R.string.section_other_title));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_SETTINGS, R.string.navigation_settings_title, R.drawable.ic_setting));
            navigationItems.add(NavMenuItem.create(NavigationUtils.POSITION_LOGOUT, R.string.navigation_logout_title, R.drawable.ic_exit));

            mNavigationAdapter = new NavigationAdapter(mNavigationView.getContext(), navigationItems, mCurrentPosition);
            mNavigationMapper.registerAdapter(mNavigationAdapter);

            mNavigationView.highlightPosition(mCurrentPosition);
        }
    }

    @Override
    public void saveState(Bundle outState) {
        outState.putInt(POSITION_PARCELABLE_KEY, mCurrentPosition);
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(POSITION_PARCELABLE_KEY)) {
            mCurrentPosition = savedState.getInt(POSITION_PARCELABLE_KEY);

            savedState.remove(POSITION_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {

    }

    @Override
    public void onNavigationItemClick(int position) {
        if (position != mCurrentPosition) {
            if (position != NavigationUtils.SECTION_LIBRARY &&
                    position != NavigationUtils.SECTION_MAL &&
                    position != NavigationUtils.SECTION_OTHER &&
                    position != NavigationUtils.SECTION_PERSONAL &&
                    position != NavigationUtils.SECTION_READER) {

                if (position != NavigationUtils.HEADER_PROFILE && position != NavigationUtils.POSITION_FRIENDS && position != NavigationUtils.POSITION_SEARCH) {
                    if (mCurrentPosition == NavigationUtils.POSITION_SETTINGS && position != NavigationUtils.POSITION_SETTINGS) {
                        mNavigationView.initializeUsernameTextView(AccountService.getUsername());
                    }

                    mCurrentPosition = position;
                    mNavigationAdapter.setCurrentPosition(mCurrentPosition);

                    mNavigationView.highlightPosition(mCurrentPosition);
                }

                EventBus.getDefault().post(new NavigationItemSelectEvent(position));
            }
        }
    }

    private void downloadProfileDataFromNetwork() {
        MalbileManager.downloadUserDataFromNetwork(AccountService.getUsername(), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
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
                    public void onNext(User user) {
                        if (user != null && user.getProfile() != null)
                            mNavigationView.setThumbnail(user.getProfile().getAvatarUrl());
                    }
                });
    }
}

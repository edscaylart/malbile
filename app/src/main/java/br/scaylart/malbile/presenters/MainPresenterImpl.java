package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.events.NavigationItemSelectEvent;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.presenters.listeners.MainPresenter;
import br.scaylart.malbile.utils.NavigationUtils;
import br.scaylart.malbile.views.activities.MainActivity;
import br.scaylart.malbile.views.activities.ProfileActivity;
import br.scaylart.malbile.views.activities.SearchActivity;
import br.scaylart.malbile.views.dialogs.LogoutConfirmationDialogFragment;
import br.scaylart.malbile.views.fragments.CatalogueFragment;
import br.scaylart.malbile.views.fragments.LibraryFragment;
import br.scaylart.malbile.views.fragments.MalLibraryFragment;
import br.scaylart.malbile.views.fragments.NavigationFragment;
import br.scaylart.malbile.views.fragments.SettingsFragment;
import br.scaylart.malbile.views.listeners.MainView;
import de.greenrobot.event.EventBus;

public class MainPresenterImpl implements MainPresenter {
    public static final String TAG = MainPresenterImpl.class.getSimpleName();

    private static final String MAIN_FRAGMENT_PARCELABLE_KEY = TAG + ":" + "MainFragmentParcelableKey";
    private static final String PREFERENCE_FRAGMENT_PARCELABLE_KEY = TAG + ":" + "PreferenceFragmentParcelableKey";

    private MainView mMainView;

    private Fragment mFragment;
    private PreferenceFragment mPreferenceFragment;

    private int mInitialPosition;

    public MainPresenterImpl(MainView mainView) {
        mMainView = mainView;
    }

    @Override
    public void initializeViews() {
        mMainView.initializeToolbar();
        mMainView.initializeDrawerLayout();
    }

    @Override
    public void initializeMainLayout(Intent argument) {
        // TODO ajustar
        if (argument != null) {
            if (argument.hasExtra(MainActivity.POSITION_ARGUMENT_KEY)) {
                mInitialPosition = argument.getIntExtra(MainActivity.POSITION_ARGUMENT_KEY, NavigationUtils.POSITION_ANIME);

                if (mInitialPosition == NavigationUtils.POSITION_ANIME) {
                    mFragment = LibraryFragment.newInstance(BaseService.ListType.ANIME, AccountService.getUsername());
                } else if (mInitialPosition == NavigationUtils.POSITION_MANGA) {
                    mFragment = LibraryFragment.newInstance(BaseService.ListType.MANGA, AccountService.getUsername());
                } else if (mInitialPosition == NavigationUtils.POSITION_JUST_ADDED) {
                    mFragment = MalLibraryFragment.newInstance(MalbileManager.TaskJob.JUSTADDED, AccountService.getUsername());
                } else if (mInitialPosition == NavigationUtils.POSITION_MOST_POPULAR) {
                    mFragment = MalLibraryFragment.newInstance(MalbileManager.TaskJob.MOSTPOPULAR, AccountService.getUsername());
                } else if (mInitialPosition == NavigationUtils.POSITION_TOP_RATED) {
                    mFragment = MalLibraryFragment.newInstance(MalbileManager.TaskJob.TOPRATED, AccountService.getUsername());
                } else if (mInitialPosition == NavigationUtils.POSITION_UPCOMING) {
                    mFragment = MalLibraryFragment.newInstance(MalbileManager.TaskJob.UPCOMING, AccountService.getUsername());
                }

                argument.removeExtra(MainActivity.POSITION_ARGUMENT_KEY);
            }
        }

        if (mFragment == null) {
            mInitialPosition = NavigationUtils.POSITION_ANIME;
            mFragment = LibraryFragment.newInstance(BaseService.ListType.ANIME, AccountService.getUsername());
        }

        ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .add(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

    @Override
    public void initializeNavigationLayout() {
        Fragment navigationFragment = NavigationFragment.newInstance(mInitialPosition);

        ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .add(mMainView.getNavigationLayoutId(), navigationFragment)
                .commit();
    }

    @Override
    public void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(NavigationItemSelectEvent event) {
        if (event != null) {
            mMainView.closeDrawerLayout();

            int position = event.getSelectedPosition();

            if (position == NavigationUtils.HEADER_PROFILE) {
                onPositionProfile(false);
            } else if (position == NavigationUtils.POSITION_ANIME) {
                onPositionLibrary(BaseService.ListType.ANIME);
            } else if (position == NavigationUtils.POSITION_MANGA) {
                onPositionLibrary(BaseService.ListType.MANGA);
            } else if (position == NavigationUtils.POSITION_FRIENDS) {
                onPositionProfile(true);
            } else if (position == NavigationUtils.POSITION_CATALOGUE) {
                onPositionCatalogue();
            } else if (position == NavigationUtils.POSITION_SEARCH) {
                onPositionSearch();
            } else if (position == NavigationUtils.POSITION_MOST_POPULAR) {
                onMalLibrary(MalbileManager.TaskJob.MOSTPOPULAR);
            } else if (position == NavigationUtils.POSITION_TOP_RATED) {
                onMalLibrary(MalbileManager.TaskJob.TOPRATED);
            } else if (position == NavigationUtils.POSITION_JUST_ADDED) {
                onMalLibrary(MalbileManager.TaskJob.JUSTADDED);
            } else if (position == NavigationUtils.POSITION_UPCOMING) {
                onMalLibrary(MalbileManager.TaskJob.UPCOMING);
            } else if (position == NavigationUtils.POSITION_SETTINGS) {
                onPositionSettings();
            } else if (position == NavigationUtils.POSITION_LOGOUT) {
                onPositionLogout();
            }
        }
    }

    @Override
    public void unregisterForEvents() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void saveState(Bundle outState) {
        if (mFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT_PARCELABLE_KEY, mFragment);
        }
        if (mPreferenceFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getFragmentManager().putFragment(outState, PREFERENCE_FRAGMENT_PARCELABLE_KEY, mPreferenceFragment);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(MAIN_FRAGMENT_PARCELABLE_KEY)) {
            mFragment = ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().getFragment(savedState, MAIN_FRAGMENT_PARCELABLE_KEY);

            savedState.remove(MAIN_FRAGMENT_PARCELABLE_KEY);
        }
        if (savedState.containsKey(PREFERENCE_FRAGMENT_PARCELABLE_KEY)) {
            mPreferenceFragment = (PreferenceFragment) ((FragmentActivity) mMainView.getContext()).getFragmentManager().getFragment(savedState, PREFERENCE_FRAGMENT_PARCELABLE_KEY);

            savedState.remove(PREFERENCE_FRAGMENT_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        // do nothing
    }

    private void onPositionLibrary(BaseService.ListType listType) {
        mFragment = LibraryFragment.newInstance(listType, AccountService.getUsername());

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onMalLibrary(MalbileManager.TaskJob taskJob) {
        mFragment = MalLibraryFragment.newInstance(taskJob, AccountService.getUsername());

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionCatalogue() {
        mFragment = new CatalogueFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionSettings() {
        mPreferenceFragment = new SettingsFragment();

        removeMainFragment();
        replacePreferenceFragment();
    }

    private void onPositionProfile(boolean friends) {
        Intent profileIntent = ProfileActivity.constructProfileActivityIntent(mMainView.getContext(), AccountService.getUsername());
        if (friends)
            profileIntent.putExtra("friends", true);
        mMainView.getContext().startActivity(profileIntent);
    }

    private void onPositionSearch() {
        Intent searchIntent = SearchActivity.constructSearchActivityIntent(mMainView.getContext(), AccountService.getUsername());
        mMainView.getContext().startActivity(searchIntent);
    }

    private void onPositionLogout() {
        LogoutConfirmationDialogFragment lcdf = new LogoutConfirmationDialogFragment();
        lcdf.show(((FragmentActivity) mMainView.getContext()).getSupportFragmentManager(), "fragment_LogoutConfirmationDialog");
    }

    private void removePreferenceFragment() {
        if (mPreferenceFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getFragmentManager().beginTransaction()
                    .remove(mPreferenceFragment)
                    .commit();

            mPreferenceFragment = null;
        }
    }

    private void replaceMainFragment() {
        ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

    private void removeMainFragment() {
        if (mFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                    .remove(mFragment)
                    .commit();

            mFragment = null;
        }
    }

    private void replacePreferenceFragment() {
        ((FragmentActivity) mMainView.getContext()).getFragmentManager().beginTransaction()
                .replace(mMainView.getMainLayoutId(), mPreferenceFragment)
                .commit();
    }
}

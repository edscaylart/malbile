package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import br.scaylart.malbile.R;
import br.scaylart.malbile.presenters.listeners.UserLibraryPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.LibraryActivity;
import br.scaylart.malbile.views.fragments.LibraryFragment;
import br.scaylart.malbile.views.listeners.UserLibraryView;

public class UserLibraryPresenterImpl implements UserLibraryPresenter {
    public static final String TAG = UserLibraryPresenterImpl.class.getSimpleName();
    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";

    private UserLibraryView mUserLibraryView;
    private RequestWrapper mRequestWrapper;

    private Fragment mFragment;

    public UserLibraryPresenterImpl(UserLibraryView userLibraryView) {
        mUserLibraryView = userLibraryView;
    }

    @Override
    public void initializeViews() {
        mUserLibraryView.initializeToolbar();

        mFragment = LibraryFragment.newInstance(mRequestWrapper.getListType(), mRequestWrapper.getUsername());

        ((FragmentActivity) mUserLibraryView.getContext()).getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, mFragment)
                .commit();
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(LibraryActivity.REQUEST_ARGUMENT_KEY)) {
                mRequestWrapper = arguments.getParcelable(LibraryActivity.REQUEST_ARGUMENT_KEY);
                arguments.remove(LibraryActivity.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void saveState(Bundle outState) {
        if (mRequestWrapper != null) {
            outState.putParcelable(REQUEST_PARCELABLE_KEY, mRequestWrapper);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(REQUEST_PARCELABLE_KEY)) {
            mRequestWrapper = savedState.getParcelable(REQUEST_PARCELABLE_KEY);
            savedState.remove(REQUEST_PARCELABLE_KEY);
        }
    }

    private void replaceMainFragment() {
        ((FragmentActivity) mUserLibraryView.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, mFragment)
                .commit();
    }
}

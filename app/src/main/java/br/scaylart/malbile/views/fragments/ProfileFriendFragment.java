package br.scaylart.malbile.views.fragments;

import android.os.Bundle;

import br.scaylart.malbile.presenters.ProfileFriendPresenterImpl;

public class ProfileFriendFragment extends BaseListFragment {
    public static final String TAG = ProfileFriendFragment.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    public static ProfileFriendFragment newInstance(String username) {
        ProfileFriendFragment newInstance = new ProfileFriendFragment();

        Bundle arguments = new Bundle();
        arguments.putString(USERNAME_ARGUMENT_KEY, username);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mLibraryPresenter = new ProfileFriendPresenterImpl(this, this);
        }
    }
}

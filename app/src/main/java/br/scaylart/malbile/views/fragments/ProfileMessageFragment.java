package br.scaylart.malbile.views.fragments;

import android.os.Bundle;

import br.scaylart.malbile.presenters.ProfileMessagePresenterImpl;

public class ProfileMessageFragment extends BaseListFragment {
    public static final String TAG = ProfileMessageFragment.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    public static ProfileMessageFragment newInstance(String username) {
        ProfileMessageFragment newInstance = new ProfileMessageFragment();

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
            mLibraryPresenter = new ProfileMessagePresenterImpl(this, this);
        }
    }
}

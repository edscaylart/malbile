package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.presenters.listeners.ProfileFragmentPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.LibraryActivity;
import br.scaylart.malbile.views.fragments.ProfileHomeFragment;
import br.scaylart.malbile.views.listeners.ProfileFragmentView;

public class ProfileHomePresenterImpl implements ProfileFragmentPresenter {
    public static final String TAG = ProfileHomePresenterImpl.class.getSimpleName();
    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    private String mUsername;
    private ProfileFragmentView mProfileFragmentView;

    public ProfileHomePresenterImpl(ProfileFragmentView profileFragmentView) {
        mProfileFragmentView = profileFragmentView;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(ProfileHomeFragment.USERNAME_ARGUMENT_KEY)) {
                mUsername = arguments.getString(ProfileHomeFragment.USERNAME_ARGUMENT_KEY);
                arguments.remove(ProfileHomeFragment.USERNAME_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mProfileFragmentView.initializeViews();
    }

    @Override
    public void initializeData() {
        mProfileFragmentView.initializeData(mUsername);
    }

    @Override
    public void openLibrary(BaseService.ListType listType) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(LibraryActivity.REQUEST_ARGUMENT_KEY, new RequestWrapper(listType, 0, mUsername));

        Intent profileIntent = LibraryActivity.constructLibraryActivityIntent(mProfileFragmentView.getContext(), arguments);
        mProfileFragmentView.getContext().startActivity(profileIntent);
    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

    }

    @Override
    public void saveState(Bundle outState) {
        if (mUsername != null) {
            outState.putString(USERNAME_ARGUMENT_KEY, mUsername);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(USERNAME_ARGUMENT_KEY)) {
            mUsername = savedState.getString(USERNAME_ARGUMENT_KEY);

            savedState.remove(USERNAME_ARGUMENT_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {

    }

    @Override
    public void releaseAllResources() {

    }

    @Override
    public void onClick(View v) {

    }
}

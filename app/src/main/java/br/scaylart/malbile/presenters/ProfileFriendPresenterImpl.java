package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.presenters.listeners.LibraryPresenter;
import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.ProfileActivity;
import br.scaylart.malbile.views.adapters.FriendsAdapter;
import br.scaylart.malbile.views.fragments.ProfileFriendFragment;
import br.scaylart.malbile.views.listeners.LibraryView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProfileFriendPresenterImpl implements LibraryPresenter {
    public static final String TAG = ProfileFriendPresenterImpl.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";
    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    protected String mUsername;

    protected LibraryView mLibraryView;
    protected LibraryMapper mLibraryMapper;
    protected FriendsAdapter mLibraryAdapter;

    protected Parcelable mPositionSavedState;

    protected Subscription mQueryUserSubscription;

    public ProfileFriendPresenterImpl(LibraryView libraryView, LibraryMapper libraryMapper) {
        mLibraryView = libraryView;
        mLibraryMapper = libraryMapper;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(ProfileFriendFragment.USERNAME_ARGUMENT_KEY)) {
                mUsername = arguments.getString(ProfileFriendFragment.USERNAME_ARGUMENT_KEY);
                arguments.remove(ProfileFriendFragment.USERNAME_ARGUMENT_KEY);
            }
        }
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
            mLibraryAdapter = new FriendsAdapter(mLibraryView.getContext(), mUsername);
            mLibraryMapper.registerAdapter(mLibraryAdapter);

            queryReviewFromNetwork();
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
        if (mUsername != null) {
            outState.putString(USERNAME_ARGUMENT_KEY, mUsername);
        }
        if (mLibraryMapper.getPositionState() != null) {
            outState.putParcelable(POSITION_PARCELABLE_KEY, mLibraryMapper.getPositionState());
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(USERNAME_ARGUMENT_KEY)) {
            mUsername = savedState.getString(USERNAME_ARGUMENT_KEY);
            savedState.remove(USERNAME_ARGUMENT_KEY);
        }
        if (savedState.containsKey(POSITION_PARCELABLE_KEY)) {
            mPositionSavedState = savedState.getParcelable(POSITION_PARCELABLE_KEY);
            savedState.remove(POSITION_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
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
            User record = mLibraryAdapter.getDataByIndex(position);

            if (record != null) {
                Intent profileIntent = ProfileActivity.constructProfileActivityIntent(mLibraryView.getContext(), record.getUsername());
                mLibraryView.getContext().startActivity(profileIntent);
            }
        }
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void searchFromNetwork(String query) {

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

    public void queryReviewFromNetwork() {
        if (mQueryUserSubscription != null) {
            mQueryUserSubscription.unsubscribe();
            mQueryUserSubscription = null;
        }

        mQueryUserSubscription = MalbileManager
                .downloadFriendDataFromNetwork(mUsername)
                .flatMap(new Func1<ArrayList<User>, Observable<ArrayList<User>>>() {
                    @Override
                    public Observable<ArrayList<User>> call(ArrayList<User> users) {
                        return QueryManager.queryStoreFriendsPofile(users, mUsername);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<User>>() {
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
                    public void onNext(ArrayList<User> users) {
                        if (mLibraryAdapter != null) {
                            mLibraryAdapter.setRecords(users);
                        }

                        if (users != null && users.size() > 0) {
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

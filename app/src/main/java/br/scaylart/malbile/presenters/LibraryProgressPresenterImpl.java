package br.scaylart.malbile.presenters;

import android.os.Bundle;
import android.os.Parcelable;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.AnimeList;
import br.scaylart.malbile.presenters.listeners.LibraryPresenter;
import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.adapters.LibraryAdapter;
import br.scaylart.malbile.views.fragments.LibraryFragment;
import br.scaylart.malbile.views.fragments.library.InProgressFragment;
import br.scaylart.malbile.views.listeners.LibraryView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LibraryProgressPresenterImpl extends BaseLibraryPresenterImpl {
    public static final String TAG = LibraryProgressPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";
    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    public LibraryProgressPresenterImpl(LibraryView libraryView, LibraryMapper libraryMapper) {
        mLibraryView = libraryView;
        mLibraryMapper = libraryMapper;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(InProgressFragment.REQUEST_ARGUMENT_KEY)) {
                mLibraryWrapper = arguments.getParcelable(InProgressFragment.REQUEST_ARGUMENT_KEY);
                arguments.remove(InProgressFragment.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public String getRequestParcelableKey(){
        return REQUEST_PARCELABLE_KEY;
    }

    @Override
    public String getPositionParcelableKey(){
        return POSITION_PARCELABLE_KEY;
    }

}

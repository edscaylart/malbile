package br.scaylart.malbile.presenters;

import android.os.Bundle;

import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.views.fragments.library.AnimeMalFragment;
import br.scaylart.malbile.views.fragments.library.AnimeSearchFragment;
import br.scaylart.malbile.views.listeners.LibraryView;

public class LibraryAnimeSearchPresenterImpl extends BaseLibraryPresenterImpl {
    public static final String TAG = LibraryAnimeSearchPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";
    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    public LibraryAnimeSearchPresenterImpl(LibraryView libraryView, LibraryMapper libraryMapper) {
        mLibraryView = libraryView;
        mLibraryMapper = libraryMapper;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(AnimeSearchFragment.REQUEST_ARGUMENT_KEY)) {
                mLibraryWrapper = arguments.getParcelable(AnimeSearchFragment.REQUEST_ARGUMENT_KEY);
                arguments.remove(AnimeSearchFragment.REQUEST_ARGUMENT_KEY);
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

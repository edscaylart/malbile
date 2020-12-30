package br.scaylart.malbile.presenters;

import android.os.Bundle;

import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.views.fragments.library.MangaMalFragment;
import br.scaylart.malbile.views.fragments.library.MangaSearchFragment;
import br.scaylart.malbile.views.listeners.LibraryView;

public class LibraryMangaSearchPresenterImpl extends BaseLibraryPresenterImpl {
    public static final String TAG = LibraryMangaSearchPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";
    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    public LibraryMangaSearchPresenterImpl(LibraryView libraryView, LibraryMapper libraryMapper) {
        mLibraryView = libraryView;
        mLibraryMapper = libraryMapper;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(MangaSearchFragment.REQUEST_ARGUMENT_KEY)) {
                mLibraryWrapper = arguments.getParcelable(MangaSearchFragment.REQUEST_ARGUMENT_KEY);
                arguments.remove(MangaSearchFragment.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public String getRequestParcelableKey() {
        return REQUEST_PARCELABLE_KEY;
    }

    @Override
    public String getPositionParcelableKey() {
        return POSITION_PARCELABLE_KEY;
    }
}

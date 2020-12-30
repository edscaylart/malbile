package br.scaylart.malbile.views.fragments.library;

import android.os.Bundle;

import br.scaylart.malbile.presenters.LibraryMangaPresenterImpl;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.fragments.BaseListFragment;

public class MangaMalFragment extends BaseListFragment {
    public static final String TAG = MangaMalFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    public static MangaMalFragment newInstance(LibraryWrapper mRequest) {
        MangaMalFragment newInstance = new MangaMalFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(REQUEST_ARGUMENT_KEY, mRequest);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mLibraryPresenter = new LibraryMangaPresenterImpl(this, this);
        }
    }
}

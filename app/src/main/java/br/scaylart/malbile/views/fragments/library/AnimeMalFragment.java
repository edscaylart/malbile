package br.scaylart.malbile.views.fragments.library;

import android.os.Bundle;

import br.scaylart.malbile.presenters.LibraryAnimePresenterImpl;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.fragments.BaseListFragment;

public class AnimeMalFragment extends BaseListFragment {
    public static final String TAG = AnimeMalFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    public static AnimeMalFragment newInstance(LibraryWrapper mRequest) {
        AnimeMalFragment newInstance = new AnimeMalFragment();

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
            mLibraryPresenter = new LibraryAnimePresenterImpl(this, this);
        }
    }
}

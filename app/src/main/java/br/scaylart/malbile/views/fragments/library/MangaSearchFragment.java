package br.scaylart.malbile.views.fragments.library;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.controllers.networks.SearchReceiver;
import br.scaylart.malbile.controllers.networks.interfaces.SearchListener;
import br.scaylart.malbile.presenters.LibraryMangaSearchPresenterImpl;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.fragments.BaseListFragment;

public class MangaSearchFragment extends BaseListFragment implements SearchListener {
    public static final String TAG = MangaSearchFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    SearchReceiver searchReceiver;
    Activity activity;

    public static MangaSearchFragment newInstance(LibraryWrapper mRequest) {
        MangaSearchFragment newInstance = new MangaSearchFragment();

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
            mLibraryPresenter = new LibraryMangaSearchPresenterImpl(this, this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        searchReceiver = new SearchReceiver(this);
        IntentFilter filter = new IntentFilter(SearchReceiver.RECEIVER);
        LocalBroadcastManager.getInstance(activity).registerReceiver(searchReceiver, filter);
    }

    @Override
    public void onDetach() {
        if (searchReceiver != null)
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(searchReceiver);
        super.onDetach();
    }

    @Override
    public void onSearchQuery(BaseService.ListType type, String query) {
        if (type.equals(BaseService.ListType.MANGA))
            mLibraryPresenter.searchFromNetwork(query);
    }
}

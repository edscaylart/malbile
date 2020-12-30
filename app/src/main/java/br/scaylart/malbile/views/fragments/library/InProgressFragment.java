package br.scaylart.malbile.views.fragments.library;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.controllers.networks.StatusUpdatedReceiver;
import br.scaylart.malbile.controllers.networks.interfaces.StatusUpdateListener;
import br.scaylart.malbile.presenters.LibraryProgressPresenterImpl;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.fragments.BaseListFragment;

public class InProgressFragment extends BaseListFragment implements StatusUpdateListener {
    public static final String TAG = InProgressFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    StatusUpdatedReceiver statusReceiver;
    Activity activity;

    public static InProgressFragment newInstance(LibraryWrapper mRequest) {
        InProgressFragment newInstance = new InProgressFragment();

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
            mLibraryPresenter = new LibraryProgressPresenterImpl(this, this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        statusReceiver = new StatusUpdatedReceiver(this);
        IntentFilter filter = new IntentFilter(StatusUpdatedReceiver.RECEIVER);
        LocalBroadcastManager.getInstance(activity).registerReceiver(statusReceiver, filter);
    }

    @Override
    public void onDetach() {
        if (statusReceiver != null)
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(statusReceiver);
        super.onDetach();
    }

    @Override
    public void onStatusUpdated(BaseService.ListType type) {
        mLibraryPresenter.refreshData();
    }
}

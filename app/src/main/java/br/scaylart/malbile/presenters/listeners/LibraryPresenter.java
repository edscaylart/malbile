package br.scaylart.malbile.presenters.listeners;

import android.app.Activity;
import android.os.Bundle;

import br.scaylart.malbile.controllers.networks.interfaces.StatusUpdateListener;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;

public interface LibraryPresenter {
    void handleInitialArguments(Bundle arguments);

    void initializeViews();

    void initializeSearch();

    void initializeData();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void releaseAllResources();

    void onRecordClick(int position);

    void refreshData();

    void searchFromNetwork(String query);

    void onOptionFilter();

    void onOptionToTop();

    RequestWrapper getRequestWrapper();
}

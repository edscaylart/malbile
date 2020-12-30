package br.scaylart.malbile.presenters.listeners;

import android.os.Bundle;
import android.view.View;

import br.scaylart.malbile.controllers.networks.BaseService;

public interface ProfileFragmentPresenter extends View.OnClickListener {
    void handleInitialArguments(Bundle arguments);

    void initializeViews();

    void initializeData();

    void openLibrary(BaseService.ListType listType);

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void releaseAllResources();
}

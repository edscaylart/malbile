package br.scaylart.malbile.presenters.listeners;

import android.content.Intent;
import android.os.Bundle;

import br.scaylart.malbile.utils.wrappers.RequestWrapper;

public interface DetailPresenter {
    void handleInitialArguments(Intent arguments);

    void initializeViews();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void onResume();

    void updateInformations();

    void deleteInformations();

    RequestWrapper getRequestWrapper();
}

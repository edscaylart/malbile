package br.scaylart.malbile.presenters.listeners;

import android.content.Intent;
import android.os.Bundle;

public interface ProfilePresenter {
    void handleInitialArguments(Intent arguments);

    void initializeViews();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void onResume();
}

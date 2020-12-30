package br.scaylart.malbile.presenters.listeners;

import android.os.Bundle;

public interface NavigationPresenter {
    void handleInitialArguments(Bundle arguments);

    void initializeViews();

    void initializeNavigationFromResources();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void onNavigationItemClick(int position);
}

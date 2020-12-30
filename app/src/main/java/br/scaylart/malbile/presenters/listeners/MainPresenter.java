package br.scaylart.malbile.presenters.listeners;

import android.content.Intent;
import android.os.Bundle;

public interface MainPresenter {
    void initializeViews();

    void initializeMainLayout(Intent argument);

    void initializeNavigationLayout();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();
}

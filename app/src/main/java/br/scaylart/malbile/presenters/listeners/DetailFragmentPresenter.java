package br.scaylart.malbile.presenters.listeners;

import android.os.Bundle;
import android.view.View;

public interface DetailFragmentPresenter extends View.OnClickListener {
    void handleInitialArguments(Bundle arguments);

    void initializeViews();

    void initializeData();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void releaseAllResources();
}

package br.scaylart.malbile.presenters.listeners;

import android.os.Bundle;

public interface UserLibraryPresenter {
    void initializeViews();

    void handleInitialArguments(Bundle arguments);

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);
}

package br.scaylart.malbile.presenters.listeners;

import android.os.Bundle;

public interface ResumeChapterPresenter {
    void handleInitialArguments(Bundle arguments);

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void onYesButtonClick();

    void onNoButtonClick();
}

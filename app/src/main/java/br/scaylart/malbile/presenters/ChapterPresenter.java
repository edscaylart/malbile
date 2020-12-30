package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public interface ChapterPresenter {
    void handleInitialArguments(Intent arguments);

    void initializeViews();

    void initializeOptions();

    void initializeMenu();

    void initializeDataFromUrl(FragmentManager fragmentManager);

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void saveChapterToRecentChapters();

    void destroyAllSubscriptions();

    void onTrimMemory(int level);

    void onLowMemory();

    void onPageSelected(int position);

    void onFirstPageOut();

    void onLastPageOut();

    void onPreviousClick();

    void onNextClick();

    void onOptionParent();

    void onOptionRefresh();

    void onOptionSelectPage();

    void onOptionDirection();

    void onOptionOrientation();

    void onOptionZoom();

    void onOptionHelp();
}

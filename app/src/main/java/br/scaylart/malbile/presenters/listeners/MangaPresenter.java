package br.scaylart.malbile.presenters.listeners;

import android.content.Intent;
import android.os.Bundle;

public interface MangaPresenter {
    void handleInitialArguments(Intent arguments);

    void initializeViews();

    void initializeDataFromUrl();

    void registerForEvents();

    void unregisterForEvents();

    void onResume();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void releaseAllResources();

    void onApplyColorChange(int color);

    void onSwipeRefresh();

    void onChapterClick(int position);

    void onFavourite();

    void onOptionRefresh();

    void onOptionMarkRead();

    void onOptionDownload();

    void onOptionToTop();

    void onOptionDelete();

    void onOptionSelectAll();

    void onOptionClear();
}

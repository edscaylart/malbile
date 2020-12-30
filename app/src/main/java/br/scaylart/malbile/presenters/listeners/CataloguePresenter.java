package br.scaylart.malbile.presenters.listeners;

import android.os.Bundle;

public interface CataloguePresenter {
    void initializeViews();

    void initializeSearch();

    void initializeDataFromPreferenceSource();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void releaseAllResources();

    void onMangaClick(int position);

    void onQueryTextChange(String query);

    void onPreviousClick();

    void onNextClick();

    void onOptionFilter();

    void onOptionToTop();
}
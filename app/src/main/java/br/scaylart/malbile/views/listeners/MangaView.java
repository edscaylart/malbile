package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.base.BaseAbsListViewView;
import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseEmptyRelativeLayoutView;
import br.scaylart.malbile.views.base.BaseSelectionView;
import br.scaylart.malbile.views.base.BaseSwipeRefreshLayoutView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface MangaView extends BaseContextView, BaseToolbarView, BaseSwipeRefreshLayoutView, BaseEmptyRelativeLayoutView, BaseAbsListViewView, BaseSelectionView {
    void initializeDeletionListView();

    void initializeFavouriteButton(boolean isFavourite);

    void showListViewIfHidden();

    void showChapterStatusError();

    void hideChapterStatusError();

    void setTitle(String title);

    void setName(String name);

    void setDescription(String description);

    void setAuthor(String author);

    void setArtist(String artist);

    void setGenre(String genre);

    void setIsCompleted(boolean isCompleted);

    void setThumbnail(String url);

    void setFavouriteButton(boolean isFavourite);

    int getHeaderViewsCount();

    void toastMangaError();
}
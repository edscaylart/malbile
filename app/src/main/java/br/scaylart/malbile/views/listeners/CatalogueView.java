package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.base.BaseAbsListViewView;
import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseEmptyRelativeLayoutView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface CatalogueView extends BaseContextView, BaseToolbarView, BaseEmptyRelativeLayoutView, BaseAbsListViewView {
    void initializeButtons();

    void setSubtitlePositionText(int position);

    void toastNoPreviousPage();

    void toastNoNextPage();

}
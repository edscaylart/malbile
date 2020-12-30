package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface ReviewView  extends BaseContextView, BaseToolbarView {
    void initializeViews(RequestWrapper request, int progress, String title);

    void hideProgressDialog();

    void closeActivity();
}
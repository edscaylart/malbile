package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface MessageView extends BaseContextView, BaseToolbarView {
    void initializeViews(String username);

    void hideProgressDialog();

    void closeActivity();
}

package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface MainView extends BaseContextView, BaseToolbarView {
    void initializeDrawerLayout();

    void closeDrawerLayout();

    int getNavigationLayoutId();

    int getMainLayoutId();
}
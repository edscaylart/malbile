package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.base.BaseAbsListViewView;
import br.scaylart.malbile.views.base.BaseContextView;

public interface NavigationView extends BaseContextView, BaseAbsListViewView {
    void initializeUsernameTextView(String source);

    void setThumbnail(String url);

    void highlightPosition(int position);
}
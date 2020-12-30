package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;

public interface DetailFragmentView {
    DetailActivity getContext();

    void initializeViews();

    void initializeData(RequestWrapper request);
}

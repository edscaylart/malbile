package br.scaylart.malbile.controllers.events;

import br.scaylart.malbile.utils.wrappers.SearchCatalogueWrapper;

public class SearchCatalogueWrapperSubmitEvent {
    private SearchCatalogueWrapper mSearchCatalogueWrapper;

    public SearchCatalogueWrapperSubmitEvent(SearchCatalogueWrapper searchCatalogueWrapper) {
        mSearchCatalogueWrapper = searchCatalogueWrapper;
    }

    public SearchCatalogueWrapper getSearchCatalogueWrapper() {
        return mSearchCatalogueWrapper;
    }
}

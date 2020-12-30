package br.scaylart.malbile.controllers.networks.interfaces;

import br.scaylart.malbile.controllers.networks.BaseService;

public interface SearchListener {
    void onSearchQuery(BaseService.ListType type, String query);
}

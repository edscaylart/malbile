package br.scaylart.malbile.controllers.networks.interfaces;

import br.scaylart.malbile.controllers.networks.BaseService;

public interface StatusUpdateListener {
    void onStatusUpdated(BaseService.ListType type);
}

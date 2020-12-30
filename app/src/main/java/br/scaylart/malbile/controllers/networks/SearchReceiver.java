package br.scaylart.malbile.controllers.networks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.scaylart.malbile.controllers.networks.interfaces.SearchListener;

public class SearchReceiver extends BroadcastReceiver {
    public static final String RECEIVER = "br.scaylart.malbile.controllers.networks.SearchReceiver";
    private SearchListener callback;

    public SearchReceiver(SearchListener callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(RECEIVER)) {
            if (callback != null) {
                BaseService.ListType listType = (BaseService.ListType) intent.getSerializableExtra("type");
                String query = intent.getStringExtra("query");
                callback.onSearchQuery(listType, query);
            }
        }
    }
}
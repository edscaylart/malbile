package br.scaylart.malbile.controllers.networks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.scaylart.malbile.controllers.networks.interfaces.StatusUpdateListener;

public class StatusUpdatedReceiver extends BroadcastReceiver {
    public static final String RECEIVER = "br.scaylart.malbile.controllers.networks.StatusUpdatedReceiver";
    private StatusUpdateListener callback;

    public StatusUpdatedReceiver(StatusUpdateListener callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(RECEIVER)) {
            if (callback != null) {
                BaseService.ListType listType = (BaseService.ListType) intent.getSerializableExtra("type");
                callback.onStatusUpdated(listType);
            }
        }
    }
}
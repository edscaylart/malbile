package br.scaylart.malbile.presenters.listeners;

import android.content.Intent;
import android.os.Bundle;

import org.apache.http.NameValuePair;

import java.util.List;

public interface MessagePresenter {
    void handleInitialArguments(Intent arguments);

    void initializeViews();

    void registerForEvents();

    void unregisterForEvents();

    void saveState(Bundle outState);

    void restoreState(Bundle savedState);

    void destroyAllSubscriptions();

    void onResume();

    void sendMessage(List<NameValuePair> nameValuePairList);

    void sendMessage(String subject, String msg);
}

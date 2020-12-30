package br.scaylart.malbile.presenters.listeners;

import android.preference.Preference;

public interface SettingsPresenter {
    void initializeDownloadDirectory();

    void initializeViews();

    boolean onPreferenceClick(Preference preference);
}

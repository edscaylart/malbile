package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface SettingsView extends BaseContextView, BaseToolbarView {
    void toastClearedImageCache();

    void toastExternalStorageError();
}

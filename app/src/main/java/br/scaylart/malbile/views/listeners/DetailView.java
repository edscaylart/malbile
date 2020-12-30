package br.scaylart.malbile.views.listeners;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseEmptyRelativeLayoutView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface DetailView extends BaseContextView, BaseToolbarView, BaseEmptyRelativeLayoutView {
    void initializeViews(RequestWrapper request);

    void setAnimeRecord(Anime record);

    void setMangaRecord(Manga record);

    Anime getAnimeRecord();

    Manga getMangaRecord();

    void setTitle(String title);

    boolean isAdded(RequestWrapper request);

    void setMenu(RequestWrapper request);

    void showDialog(String tag, DialogFragment dialog, Bundle args);

    void showDialog(String tag, DialogFragment dialog);

    void showProgressDialog(int titleString, int msgString);

    void hideProgressDialog();

    void updateInformation();

    void onUpdatedInformation();

    void closeActivity();
}

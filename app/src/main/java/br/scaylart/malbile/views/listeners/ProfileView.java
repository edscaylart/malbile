package br.scaylart.malbile.views.listeners;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import br.scaylart.malbile.models.User;
import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseEmptyRelativeLayoutView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface ProfileView extends BaseContextView, BaseToolbarView, BaseEmptyRelativeLayoutView {
    void initializeViews(String username);

    void setMenu();

    void setUserRecord(User record);

    User getUserRecord();

    void setTitle(String title);

    boolean isFriend();

    void showDialog(String tag, DialogFragment dialog, Bundle args);

    void showDialog(String tag, DialogFragment dialog);

    void showProgressDialog(int titleString, int msgString);

    void hideProgressDialog();

    void closeActivity();
}

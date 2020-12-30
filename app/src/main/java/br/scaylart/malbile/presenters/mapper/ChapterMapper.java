package br.scaylart.malbile.presenters.mapper;

import android.support.v4.view.PagerAdapter;

public interface ChapterMapper {
    void registerAdapter(PagerAdapter adapter);

    int getPosition();

    void setPosition(int position);

    void applyViewSettings();

    void applyIsLockOrientation(boolean isLockOrientation);

    void applyIsLockZoom(boolean isLockZoom);
}

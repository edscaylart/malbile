package br.scaylart.malbile.views.listeners;

import android.content.Intent;

import br.scaylart.malbile.views.base.BaseContextView;
import br.scaylart.malbile.views.base.BaseEmptyRelativeLayoutView;
import br.scaylart.malbile.views.base.BaseToolbarView;

public interface ChapterView extends BaseContextView, BaseToolbarView, BaseEmptyRelativeLayoutView {
     void initializeHardwareAcceleration();

     void initializeSystemUIVisibility();

     void initializeViewPager();

     void initializeButtons();

     void initializeTextView();

     void initializeFullscreen();

     void enableFullscreen();

    void disableFullscreen();

    void setTitleText(String title);

    void setSubtitleProgressText(int imageUrlsCount);

    void setSubtitlePositionText(int position);

    void setImmersivePositionText(int position);

    void setOptionDirectionText(boolean isRightToLeftDirection);

    void setOptionOrientationText(boolean isLockOrientation);

    void setOptionZoomText(boolean isLockZoom);

    void toastNotInitializedError();

    void toastChapterError();

    void toastNoPreviousChapter();

    void toastNoNextChapter();

    void finishAndLaunchActivity(Intent launchIntent, boolean isFadeTransition);
}

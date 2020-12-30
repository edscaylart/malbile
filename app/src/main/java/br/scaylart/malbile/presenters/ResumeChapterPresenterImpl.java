package br.scaylart.malbile.presenters;

import android.content.Intent;
import android.os.Bundle;

import br.scaylart.malbile.presenters.listeners.ResumeChapterPresenter;
import br.scaylart.malbile.reader.model.RecentChapter;
import br.scaylart.malbile.utils.wrappers.ReaderWrapper;
import br.scaylart.malbile.views.activities.ChapterActivity;
import br.scaylart.malbile.views.fragments.ResumeChapterFragment;
import br.scaylart.malbile.views.listeners.ResumeChapterView;

public class ResumeChapterPresenterImpl implements ResumeChapterPresenter {
    public static final String TAG = ResumeChapterPresenterImpl.class.getSimpleName();

    private static final String RECENT_CHAPTER_PARCELABLE_KEY = TAG + ":" + "RecentChapterParcelableKey";

    private ResumeChapterView mResumeChapterView;

    private RecentChapter mRecentChapter;

    public ResumeChapterPresenterImpl(ResumeChapterView resumeChapterView) {
        mResumeChapterView = resumeChapterView;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(ResumeChapterFragment.RECENT_CHAPTER_ARGUMENT_KEY)) {
                mRecentChapter = arguments.getParcelable(ResumeChapterFragment.RECENT_CHAPTER_ARGUMENT_KEY);

                arguments.remove(ResumeChapterFragment.RECENT_CHAPTER_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void saveState(Bundle outState) {
        if (mRecentChapter != null) {
            outState.putParcelable(RECENT_CHAPTER_PARCELABLE_KEY, mRecentChapter);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(RECENT_CHAPTER_PARCELABLE_KEY)) {
            mRecentChapter = savedState.getParcelable(RECENT_CHAPTER_PARCELABLE_KEY);

            savedState.remove(RECENT_CHAPTER_PARCELABLE_KEY);
        }
    }

    @Override
    public void onYesButtonClick() {
        if (mRecentChapter != null) {
            //Intent chapterIntent = null;
            /*if (!mRecentChapter.isOffline()) {
                chapterIntent = ChapterActivity.constructChapterActivityIntent(mResumeChapterView.getContext(), new RequestWrapper(mRecentChapter.getSource(), mRecentChapter.getUrl()), mRecentChapter.getPageNumber());
            } else {
                chapterIntent = ChapterActivity.constructOfflineChapterActivityIntent(mResumeChapterView.getContext(), new RequestWrapper(mRecentChapter.getSource(), mRecentChapter.getUrl()), mRecentChapter.getPageNumber());
            }*/
            Intent chapterIntent = ChapterActivity.constructChapterActivityIntent(mResumeChapterView.getContext(), new ReaderWrapper("MangaEden (EN)", mRecentChapter.getUrl()), mRecentChapter.getPageNumber());

            mResumeChapterView.getContext().startActivity(chapterIntent);
        }
    }

    @Override
    public void onNoButtonClick() {
        if (mRecentChapter != null) {
            //Intent chapterIntent = null;
            /*if (!mRecentChapter.isOffline()) {
                chapterIntent = ChapterActivity.constructChapterActivityIntent(mResumeChapterView.getContext(), new RequestWrapper(mRecentChapter.getSource(), mRecentChapter.getUrl()), 0);
            } else {
                chapterIntent = ChapterActivity.constructOfflineChapterActivityIntent(mResumeChapterView.getContext(), new RequestWrapper(mRecentChapter.getSource(), mRecentChapter.getUrl()), 0);
            }*/

            Intent chapterIntent = ChapterActivity.constructChapterActivityIntent(mResumeChapterView.getContext(), new ReaderWrapper("MangaEden (EN)", mRecentChapter.getUrl()), 0);

            mResumeChapterView.getContext().startActivity(chapterIntent);
        }
    }
}

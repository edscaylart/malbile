package br.scaylart.malbile.views.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import br.scaylart.malbile.controllers.MalbileManager.ListStatus;
import br.scaylart.malbile.controllers.MalbileManager.TaskJob;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.fragments.library.AnimeMalFragment;
import br.scaylart.malbile.views.fragments.library.AnimeSearchFragment;
import br.scaylart.malbile.views.fragments.library.MangaMalFragment;
import br.scaylart.malbile.views.fragments.library.MangaSearchFragment;

public class SearchPagerAdapter extends SmartFragmentStatePagerAdapter {
    public static int count = 2;
    TaskJob taskJob;
    String username;

    AnimeSearchFragment mAnimeSearchFragment;
    MangaSearchFragment mMangaSearchFragment;

    public SearchPagerAdapter(FragmentManager fm, TaskJob taskJob, String username) {
        super(fm);
        this.taskJob = taskJob;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mAnimeSearchFragment = AnimeSearchFragment.newInstance(new LibraryWrapper(ListType.ANIME, ListStatus.BLANK, taskJob, username));
                fragment = mAnimeSearchFragment;
                break;
            case 1:
                mMangaSearchFragment = MangaSearchFragment.newInstance(new LibraryWrapper(ListType.MANGA, ListStatus.BLANK, taskJob, username));
                fragment = mMangaSearchFragment;
                break;
        }
        return fragment;
    }

    public Fragment getLibrary(int i) {
        switch (i) {
            case 0:
                return mAnimeSearchFragment;
            case 1:
                return mMangaSearchFragment;
            default:
                return mAnimeSearchFragment;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Animes";
            case 1:
                return "Mangás";
            default:
                return null;
        }
    }
}
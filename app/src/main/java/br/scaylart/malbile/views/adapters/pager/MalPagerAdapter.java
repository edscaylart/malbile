package br.scaylart.malbile.views.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import br.scaylart.malbile.controllers.MalbileManager.ListStatus;
import br.scaylart.malbile.controllers.MalbileManager.TaskJob;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.adapters.pager.SmartFragmentStatePagerAdapter;
import br.scaylart.malbile.views.fragments.library.AnimeMalFragment;
import br.scaylart.malbile.views.fragments.library.MangaMalFragment;

public class MalPagerAdapter  extends SmartFragmentStatePagerAdapter {
    public static int count = 2;
    TaskJob taskJob;
    String username;

    AnimeMalFragment mAnimeMalFragment;
    MangaMalFragment mMangaMalFragment;

    public MalPagerAdapter(FragmentManager fm, TaskJob taskJob, String username) {
        super(fm);
        this.taskJob = taskJob;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mAnimeMalFragment = AnimeMalFragment.newInstance(new LibraryWrapper(ListType.ANIME, ListStatus.BLANK, taskJob, username));
                fragment = mAnimeMalFragment;
                break;
            case 1:
                mMangaMalFragment = MangaMalFragment.newInstance(new LibraryWrapper(ListType.MANGA, ListStatus.BLANK, taskJob, username));
                fragment = mMangaMalFragment;
                break;
        }
        return fragment;
    }

    public Fragment getLibrary(int i) {
        switch (i) {
            case 0:
                return mAnimeMalFragment;
            case 1:
                return mMangaMalFragment;
            default:
                return mAnimeMalFragment;
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
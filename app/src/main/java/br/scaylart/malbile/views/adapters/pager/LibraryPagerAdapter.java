package br.scaylart.malbile.views.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.views.fragments.library.CompletedFragment;
import br.scaylart.malbile.views.fragments.library.DroppedFragment;
import br.scaylart.malbile.views.fragments.library.InProgressFragment;
import br.scaylart.malbile.views.fragments.library.OnHoldFragment;
import br.scaylart.malbile.views.fragments.library.PlannedFragment;

public class LibraryPagerAdapter extends SmartFragmentStatePagerAdapter {
    public static int count = 5;
    ListType listType;
    String username;

    InProgressFragment inProgressFragment;
    PlannedFragment plannedFragment;
    OnHoldFragment onHoldFragment;
    CompletedFragment completedFragment;
    DroppedFragment droppedFragment;

    public LibraryPagerAdapter(FragmentManager fm, ListType listType, String username) {
        super(fm);
        this.listType = listType;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                inProgressFragment = InProgressFragment.newInstance(new LibraryWrapper(listType, MalbileManager.ListStatus.PROGRESS, MalbileManager.TaskJob.LIBRARY, username));
                fragment = inProgressFragment;
                break;
            case 1:
                plannedFragment = PlannedFragment.newInstance(new LibraryWrapper(listType, MalbileManager.ListStatus.PLANNED, MalbileManager.TaskJob.LIBRARY, username));
                fragment = plannedFragment;
                break;
            case 2:
                onHoldFragment = OnHoldFragment.newInstance(new LibraryWrapper(listType, MalbileManager.ListStatus.ONHOLD, MalbileManager.TaskJob.LIBRARY, username));
                fragment = onHoldFragment;
                break;
            case 3:
                completedFragment = CompletedFragment.newInstance(new LibraryWrapper(listType, MalbileManager.ListStatus.COMPLETED, MalbileManager.TaskJob.LIBRARY, username));
                fragment = completedFragment;
                break;
            case 4:
                droppedFragment = DroppedFragment.newInstance(new LibraryWrapper(listType, MalbileManager.ListStatus.DROPPED, MalbileManager.TaskJob.LIBRARY, username));
                fragment = droppedFragment;
                break;
        }
        return fragment;
    }

    public Fragment getLibrary(int i) {
        switch (i) {
            case 0:
                return inProgressFragment;
            case 1:
                return plannedFragment;
            case 2:
                return onHoldFragment;
            case 3:
                return completedFragment;
            case 4:
                return droppedFragment;
            default:
                return inProgressFragment;
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
                return MalbileApplication.getInstance().getString(listType.equals(ListType.ANIME) ? R.string.anime_status_watching : R.string.manga_status_reading);
            case 1:
                return MalbileApplication.getInstance().getString(listType.equals(ListType.ANIME) ? R.string.anime_status_plantowatch : R.string.manga_status_plantoread);
            case 2:
                return MalbileApplication.getInstance().getString(listType.equals(ListType.ANIME) ? R.string.anime_status_onhold : R.string.manga_status_onhold);
            case 3:
                return MalbileApplication.getInstance().getString(listType.equals(ListType.ANIME) ? R.string.anime_status_completed : R.string.manga_status_completed);
            case 4:
                return MalbileApplication.getInstance().getString(listType.equals(ListType.ANIME) ? R.string.anime_status_dropped : R.string.manga_status_dropped);
            default:
                return null;
        }
    }
}

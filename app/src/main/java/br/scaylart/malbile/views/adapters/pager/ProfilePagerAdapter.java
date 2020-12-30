package br.scaylart.malbile.views.adapters.pager;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.views.fragments.ProfileFriendFragment;
import br.scaylart.malbile.views.fragments.ProfileHomeFragment;
import br.scaylart.malbile.views.fragments.ProfileMessageFragment;

public class ProfilePagerAdapter extends SmartFragmentStatePagerAdapter {
    private int count = 3;
    String username;

    ProfileHomeFragment mProfileHomeFragment;
    ProfileFriendFragment mProfileFriendFragment;
    ProfileMessageFragment mProfileMessageFragment;

    public ProfilePagerAdapter(FragmentManager fm, String username) {
        super(fm);
        this.username = username;

       count = AccountService.getUsername().equals(username) ? 3 : 2;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mProfileHomeFragment = ProfileHomeFragment.newInstance(username);
                fragment = mProfileHomeFragment;
                break;
            case 1:
                mProfileFriendFragment = ProfileFriendFragment.newInstance(username);
                fragment = mProfileFriendFragment;
                break;
            case 2:
                mProfileMessageFragment = ProfileMessageFragment.newInstance(username);
                fragment = mProfileMessageFragment;
                break;
        }
        return fragment;
    }

    public Fragment getLibrary(int i) {
        switch (i) {
            case 0:
                return mProfileHomeFragment;
            case 1:
                return mProfileFriendFragment;
            case 2:
                return mProfileMessageFragment;
            default:
                return mProfileHomeFragment;
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
                return "Pefil";
            case 1:
                return "Amigos";
            case 2:
                return "Mensagens";
            default:
                return null;
        }
    }
}

package br.scaylart.malbile.views.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.fragments.DetailGeneralFragment;
import br.scaylart.malbile.views.fragments.DetailPersonalFragment;
import br.scaylart.malbile.views.fragments.DetailReviewFragment;
import br.scaylart.malbile.views.fragments.DetailUserRecsFragment;

public class DetailPagerAdapter extends SmartFragmentStatePagerAdapter {
    public static int count = 4;
    RequestWrapper mRequest;

    DetailGeneralFragment mDetailGeneralFragment;
    DetailPersonalFragment mDetailPersonalFragment;
    DetailReviewFragment mDetailReviewFragment;
    DetailUserRecsFragment mDetailUserRecsFragment;

    public DetailPagerAdapter(FragmentManager fm, RequestWrapper request) {
        super(fm);
        mRequest = request;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mDetailGeneralFragment = DetailGeneralFragment.newInstance(mRequest);
                fragment = mDetailGeneralFragment;
                break;
            case 1:
                mDetailPersonalFragment = DetailPersonalFragment.newInstance(mRequest);
                fragment = mDetailPersonalFragment;
                break;
            case 2:
                mDetailReviewFragment = DetailReviewFragment.newInstance(mRequest);
                fragment = mDetailReviewFragment;
                break;
            case 3:
                mDetailUserRecsFragment = DetailUserRecsFragment.newInstance(mRequest);
                fragment = mDetailUserRecsFragment;
                break;
        }
        return fragment;
    }

    public Fragment getLibrary(int i) {
        switch (i) {
            case 0:
                return mDetailGeneralFragment;
            case 1:
                return mDetailPersonalFragment;
            case 2:
                return mDetailReviewFragment;
            case 3:
                return mDetailUserRecsFragment;
            default:
                return mDetailGeneralFragment;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getPageTitle(int position) {
        // TODO ajustar
        switch (position) {
            case 0:
                return "Geral";
            case 1:
                return "Pessoal";
            case 2:
                return "Reviews";
            case 3:
                return "Recomendações";
            default:
                return null;
        }
    }
}
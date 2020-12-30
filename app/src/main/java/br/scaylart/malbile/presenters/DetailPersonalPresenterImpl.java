package br.scaylart.malbile.presenters;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.presenters.listeners.DetailFragmentPresenter;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.dialogs.DatePickerDialogFragment;
import br.scaylart.malbile.views.dialogs.NumberPickerDialogFragment;
import br.scaylart.malbile.views.dialogs.RatingPickerDialogFragment;
import br.scaylart.malbile.views.fragments.DetailPersonalFragment;
import br.scaylart.malbile.views.listeners.DetailFragmentView;

public class DetailPersonalPresenterImpl implements DetailFragmentPresenter {
    public static final String TAG = DetailPersonalPresenterImpl.class.getSimpleName();

    private static final String REQUEST_PARCELABLE_KEY = TAG + ":" + "RequestParcelableKey";

    private RequestWrapper mRequest;
    private DetailFragmentView mDetailFragmentView;

    public DetailPersonalPresenterImpl(DetailFragmentView detailFragmentView) {
        mDetailFragmentView = detailFragmentView;
    }

    @Override
    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(DetailPersonalFragment.REQUEST_ARGUMENT_KEY)) {
                mRequest = arguments.getParcelable(DetailPersonalFragment.REQUEST_ARGUMENT_KEY);
                arguments.remove(DetailPersonalFragment.REQUEST_ARGUMENT_KEY);
            }
        }
    }

    @Override
    public void initializeViews() {
        mDetailFragmentView.initializeViews();
    }

    @Override
    public void initializeData() {
        mDetailFragmentView.initializeData(mRequest);
    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

    }

    @Override
    public void saveState(Bundle outState) {
        if (mRequest != null) {
            outState.putParcelable(REQUEST_PARCELABLE_KEY, mRequest);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(REQUEST_PARCELABLE_KEY)) {
            mRequest = savedState.getParcelable(REQUEST_PARCELABLE_KEY);

            savedState.remove(REQUEST_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {

    }

    @Override
    public void releaseAllResources() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeStatus:
                PopupMenu popup = new PopupMenu(mDetailFragmentView.getContext(), v);
                popup.getMenuInflater().inflate(isAnime() ? R.menu.popup_anime_status : R.menu.popup_manga_status, popup.getMenu());
                popup.setOnMenuItemClickListener(mDetailFragmentView.getContext());
                popup.show();
                break;
            case R.id.relativeProgress:
                Bundle argsP = new Bundle();
                argsP.putInt("id", R.id.personalProgress);
                argsP.putInt("current", isAnime() ? mDetailFragmentView.getContext().animeRecord.getWatchedEpisodes() : mDetailFragmentView.getContext().mangaRecord.getChaptersRead());
                argsP.putInt("max", isAnime() ? mDetailFragmentView.getContext().animeRecord.getEpisodes() : mDetailFragmentView.getContext().mangaRecord.getChapters());
                argsP.putString("title", StringUtils.getString(isAnime() ? R.string.dialog_label_episodes : R.string.dialog_label_chapters));
                mDetailFragmentView.getContext().showDialog("progress", new NumberPickerDialogFragment().setOnSendClickListener(mDetailFragmentView.getContext()), argsP);
                break;
            case R.id.relativeVolume:
                Bundle argsV = new Bundle();
                argsV.putInt("id", R.id.personalVolume);
                argsV.putInt("current", mDetailFragmentView.getContext().mangaRecord.getVolumesRead());
                argsV.putInt("max", mDetailFragmentView.getContext().mangaRecord.getVolumes());
                argsV.putString("title", StringUtils.getString(R.string.dialog_label_chapters));
                mDetailFragmentView.getContext().showDialog("volumes", new NumberPickerDialogFragment().setOnSendClickListener(mDetailFragmentView.getContext()), argsV);
                break;
            case R.id.relativeRating:
                Bundle argsRT = new Bundle();
                argsRT.putInt("id", R.id.personalScore);
                argsRT.putInt("current", isAnime() ? mDetailFragmentView.getContext().animeRecord.getScore() : mDetailFragmentView.getContext().mangaRecord.getScore());
                argsRT.putString("title", StringUtils.getString(R.string.dialog_label_rating));
                mDetailFragmentView.getContext().showDialog("rating", new RatingPickerDialogFragment().setOnSendClickListener(mDetailFragmentView.getContext()), argsRT);
                break;
            case R.id.relativeStartDate:
                Bundle argsSD = new Bundle();
                argsSD.putBoolean("startDate", true);
                mDetailFragmentView.getContext().showDialog("startDate", new DatePickerDialogFragment(), argsSD);
                break;
            case R.id.relativeEndDate:
                Bundle argsED = new Bundle();
                argsED.putBoolean("startDate", false);
                mDetailFragmentView.getContext().showDialog("endDate", new DatePickerDialogFragment(), argsED);
                break;
            case R.id.relativeRewatchingCount:
                Bundle argsRC = new Bundle();
                argsRC.putInt("id", R.id.personalRewatchingCount);
                argsRC.putInt("current", isAnime() ? mDetailFragmentView.getContext().animeRecord.getRewatchingCount() : mDetailFragmentView.getContext().mangaRecord.getRereadingCount());
                argsRC.putInt("max", 999);
                argsRC.putString("title", StringUtils.getString(isAnime() ? R.string.dialog_label_rewatching : R.string.dialog_label_rereading));
                mDetailFragmentView.getContext().showDialog("progress", new NumberPickerDialogFragment().setOnSendClickListener(mDetailFragmentView.getContext()), argsRC);
                break;
            case R.id.btnUpdateInformation:
                mDetailFragmentView.getContext().showProgressDialog(R.string.dialog_title_update_info, R.string.dialog_msg_update_info);
                mDetailFragmentView.getContext().updateInformation();
        }
    }

    private boolean isAnime() {
        return mRequest.getListType().equals(BaseService.ListType.ANIME);
    }

}

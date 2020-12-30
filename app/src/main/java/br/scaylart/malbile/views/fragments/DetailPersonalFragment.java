package br.scaylart.malbile.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.presenters.DetailPersonalPresenterImpl;
import br.scaylart.malbile.presenters.listeners.DetailFragmentPresenter;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.listeners.DetailFragmentView;

public class DetailPersonalFragment extends Fragment implements DetailFragmentView {
    public static final String TAG = DetailPersonalFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    private DetailFragmentPresenter mDetailFragmentPresenter;
    private View mView;

    /**
     * Personal Fields
     */
    private RelativeLayout mRelativeVolumes;
    private TextView mLabelProgress;
    private TextView mLabelRewatching;
    private TextView mLabelRewatched;
    private TextView mTextStatus;
    private TextView mTextProgress;
    private TextView mTextVolumes;
    private TextView mTextStartDate;
    private TextView mTextEndtDate;
    private TextView mTextRewatched;
    private Switch mSwitchRewatching;
    private RatingBar mRatingScore;

    public static DetailPersonalFragment newInstance(RequestWrapper request) {
        DetailPersonalFragment newInstance = new DetailPersonalFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(REQUEST_ARGUMENT_KEY, request);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        mDetailFragmentPresenter = new DetailPersonalPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail_personal, container, false);
        mView = detailView;

        mDetailFragmentPresenter.initializeViews();

        return detailView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mDetailFragmentPresenter.restoreState(savedInstanceState);
        } else {
            mDetailFragmentPresenter.handleInitialArguments(getArguments());
        }

        mDetailFragmentPresenter.initializeData();
    }

    @Override
    public void onStart() {
        super.onStart();

        mDetailFragmentPresenter.registerForEvents();
    }

    @Override
    public void onStop() {
        mDetailFragmentPresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDetailFragmentPresenter.destroyAllSubscriptions();
        mDetailFragmentPresenter.releaseAllResources();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mDetailFragmentPresenter.saveState(outState);
    }

    @Override
    public DetailActivity getContext() {
        return (DetailActivity) getActivity();
    }

    private void setOnClickListeners(int id) {
        mView.findViewById(id).setOnClickListener(mDetailFragmentPresenter);
    }

    @Override
    public void initializeViews() {
        mRelativeVolumes = (RelativeLayout) mView.findViewById(R.id.relativeVolume);
        mLabelProgress = (TextView) mView.findViewById(R.id.labelProgress);
        mLabelRewatching = (TextView) mView.findViewById(R.id.labelRewatching);
        mLabelRewatched = (TextView) mView.findViewById(R.id.labelRewatchingCount);
        mTextStatus = (TextView) mView.findViewById(R.id.personalStatus);
        mTextProgress = (TextView) mView.findViewById(R.id.personalProgress);
        mTextVolumes = (TextView) mView.findViewById(R.id.personalVolume);
        mTextStartDate = (TextView) mView.findViewById(R.id.personalStartDate);
        mTextEndtDate = (TextView) mView.findViewById(R.id.personalEndDate);
        mTextRewatched = (TextView) mView.findViewById(R.id.personalRewatchingCount);
        mSwitchRewatching = (Switch) mView.findViewById(R.id.personalRewatching);
        mRatingScore = (RatingBar) mView.findViewById(R.id.personalScore);

        setOnClickListeners(R.id.relativeStatus);
        setOnClickListeners(R.id.relativeProgress);
        setOnClickListeners(R.id.relativeVolume);
        setOnClickListeners(R.id.relativeRating);
        setOnClickListeners(R.id.relativeStartDate);
        setOnClickListeners(R.id.relativeEndDate);
        setOnClickListeners(R.id.relativeRewatchingCount);
        setOnClickListeners(R.id.btnUpdateInformation);
        ((Switch) mView.findViewById(R.id.personalRewatching)).setOnCheckedChangeListener(getContext());
    }

    @Override
    public void initializeData(RequestWrapper request) {
        // TODO ajustar
        if (request.getListType().equals(BaseService.ListType.ANIME)) {
            mRelativeVolumes.setVisibility(View.GONE);
            mLabelProgress.setText(R.string.detail_personal_episodes);
            mLabelRewatching.setText(R.string.detail_personal_rewatching);
            mLabelRewatched.setText(R.string.detail_personal_rewatched);

            Anime record = getContext().animeRecord;

            mTextStatus.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_user_status, R.string.unknown, record.getMyStatus()));
            mTextProgress.setText(record.getProgress());
            mTextStartDate.setText(StringUtils.getDate(record.getListStartDate()));
            mTextEndtDate.setText(StringUtils.getDate(record.getListFinishDate()));

            mSwitchRewatching.setChecked(record.isRewatching());
            mTextRewatched.setText(StringUtils.nullCheck(record.getRewatchingCount()) + " times");

            mRatingScore.setRating((float) record.getScore() / 2);
        } else {
            mRelativeVolumes.setVisibility(View.VISIBLE);
            mLabelProgress.setText(R.string.detail_personal_chapters);
            mLabelRewatching.setText(R.string.detail_personal_rereading);
            mLabelRewatched.setText(R.string.detail_personal_reread);

            Manga record = getContext().mangaRecord;

            mTextStatus.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_user_status, R.string.unknown, record.getMyStatus()));
            mTextProgress.setText(record.getProgress());
            mTextVolumes.setText(record.getVolumeProgress());
            mTextStartDate.setText(StringUtils.getDate(record.getListStartDate()));
            mTextEndtDate.setText(StringUtils.getDate(record.getListFinishDate()));

            mSwitchRewatching.setChecked(record.isRereading());
            mTextRewatched.setText(StringUtils.nullCheck(record.getRereadingCount()) + " times");

            mRatingScore.setRating((float) record.getScore() / 2);
        }
    }
}

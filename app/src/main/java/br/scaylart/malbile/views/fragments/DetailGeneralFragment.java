package br.scaylart.malbile.views.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.RelatedRecord;
import br.scaylart.malbile.presenters.DetailGeneralPresenterImpl;
import br.scaylart.malbile.presenters.listeners.DetailFragmentPresenter;
import br.scaylart.malbile.utils.PaletteBitmapTarget;
import br.scaylart.malbile.utils.PaletteBitmapTranscoder;
import br.scaylart.malbile.utils.PaletteUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.PaletteBitmapWrapper;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.activities.DetailActivity;
import br.scaylart.malbile.views.listeners.DetailFragmentView;

public class DetailGeneralFragment extends Fragment implements DetailFragmentView {
    public static final String TAG = DetailGeneralFragment.class.getSimpleName();

    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    private DetailFragmentPresenter mDetailFragmentPresenter;
    private View mView;

    /**
     * General Fields
     */
    private RelativeLayout mRelativeEpisodes;
    private RelativeLayout mRelativeVolumes;
    private RelativeLayout mRelativeChapters;
    private RelativeLayout mRelativeRating;
    private ImageView mImageCover;
    private TextView mTextTitle;
    private TextView mTextEpisodes;
    private TextView mTextVolumes;
    private TextView mTextChapters;
    private TextView mTextType;
    private TextView mTextSynopsis;
    private TextView mTextStatus;
    private TextView mTextAired;
    private TextView mTextGenres;
    private TextView mTextRating;
    private TextView mTextScore;
    private TextView mTextRanked;
    private TextView mTextMembers;
    private TextView mTextFavorites;

    public static DetailGeneralFragment newInstance(RequestWrapper request) {
        DetailGeneralFragment newInstance = new DetailGeneralFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(REQUEST_ARGUMENT_KEY, request);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        mDetailFragmentPresenter = new DetailGeneralPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail_general, container, false);
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

    @Override
    public void initializeViews() {
        mRelativeEpisodes = (RelativeLayout) mView.findViewById(R.id.relativeEpisode);
        mRelativeVolumes = (RelativeLayout) mView.findViewById(R.id.relativeVolume);
        mRelativeChapters = (RelativeLayout) mView.findViewById(R.id.relativeChapters);
        mRelativeRating = (RelativeLayout) mView.findViewById(R.id.relativeRating);
        mTextTitle = (TextView) mView.findViewById(R.id.mediaTitle);
        mImageCover = (ImageView) mView.findViewById(R.id.mediaImage);
        mTextEpisodes = (TextView) mView.findViewById(R.id.mediaEpisode);
        mTextVolumes = (TextView) mView.findViewById(R.id.mediaVolume);
        mTextChapters = (TextView) mView.findViewById(R.id.mediaChapter);
        mTextType = (TextView) mView.findViewById(R.id.mediaType);
        mTextSynopsis = (TextView) mView.findViewById(R.id.mediaSynopsis);
        mTextStatus = (TextView) mView.findViewById(R.id.mediaStatus);
        mTextAired = (TextView) mView.findViewById(R.id.mediaAired);
        mTextGenres = (TextView) mView.findViewById(R.id.mediaGenres);
        mTextRating = (TextView) mView.findViewById(R.id.mediaRating);

        mTextScore = (TextView) mView.findViewById(R.id.mediaScore);
        mTextRanked = (TextView) mView.findViewById(R.id.mediaRanked);
        mTextMembers = (TextView) mView.findViewById(R.id.mediaMembers);
        mTextFavorites = (TextView) mView.findViewById(R.id.mediaFavorites);
    }

    @Override
    public void initializeData(RequestWrapper request) {
        // TODO ajustar
        BaseRecord record;
        mRelativeEpisodes.setVisibility(request.getListType().equals(ListType.ANIME) ? View.VISIBLE : View.GONE);
        mRelativeRating.setVisibility(request.getListType().equals(ListType.ANIME) ? View.VISIBLE : View.GONE);
        mRelativeVolumes.setVisibility(request.getListType().equals(ListType.ANIME) ? View.GONE : View.VISIBLE);
        mRelativeChapters.setVisibility(request.getListType().equals(ListType.ANIME) ? View.GONE : View.VISIBLE);

        if (request.getListType().equals(ListType.ANIME)) {
            record = getContext().animeRecord;
            mTextEpisodes.setText(StringUtils.nullCheck(getContext().animeRecord.getEpisodes()));
            mTextRating.setText(StringUtils.nullCheck(getContext().animeRecord.getClassification()));

            mTextType.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_type, R.string.unknown, record.getType() - 1));
            mTextStatus.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_status, R.string.unknown, record.getStatus() - 1));

            if (record.getType() != 1)
                mTextAired.setText(StringUtils.getDate(record.getStartDate()));
            else
                mTextAired.setText(StringUtils.getDate(record.getStartDate()) + " to " + StringUtils.getDate(record.getEndDate()));
        } else {
            record = getContext().mangaRecord;
            mTextVolumes.setText(StringUtils.nullCheck(getContext().mangaRecord.getVolumes()));
            mTextChapters.setText(StringUtils.nullCheck(getContext().mangaRecord.getChapters()));

            mTextType.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_type, R.string.unknown, record.getType() - 1));
            mTextStatus.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_status, R.string.unknown, record.getStatus() - 1));

            mTextAired.setText(StringUtils.getDate(record.getStartDate()) + " to " + StringUtils.getDate(record.getEndDate()));
        }
        mTextTitle.setText(StringUtils.nullCheck(record.getTitle()));

        setThumbnail(record.getImageUrl());


        mTextSynopsis.setText(StringUtils.nullCheck(record.getSynopsis()));

        mTextScore.setText(StringUtils.nullCheck(record.getMembersScore()));
        mTextRanked.setText(StringUtils.nullCheck(record.getRank()));
        mTextMembers.setText(StringUtils.nullCheck(record.getMembersCount()));
        mTextFavorites.setText(StringUtils.nullCheck(record.getFavoritedCount()));

        if (record.getGenres() != null) {
            String genres = "";
            for (RelatedRecord rrg : record.getGenres()) {
                genres += rrg.getTitle() + ", ";
            }
            genres = (genres.length() > 0) ? genres.substring(0, genres.length() - 2) : "";
            mTextGenres.setText(genres);
        }

    }

    public void setThumbnail(String url) {
        Drawable placeHolderDrawable = getResources().getDrawable(R.drawable.ic_image_white_48dp);
        placeHolderDrawable.setColorFilter(getResources().getColor(R.color.accentPinkA200), PorterDuff.Mode.MULTIPLY);
        Drawable errorHolderDrawable = getResources().getDrawable(R.drawable.ic_error_white_48dp);
        errorHolderDrawable.setColorFilter(getResources().getColor(R.color.accentPinkA200), PorterDuff.Mode.MULTIPLY);

        Glide.with(this)
                .load(url)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(), PaletteBitmapWrapper.class)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(android.R.anim.fade_in)
                .placeholder(placeHolderDrawable)
                .error(errorHolderDrawable)
                .into(new PaletteBitmapTarget(mImageCover) {
                    @Override
                    public void onResourceReady(PaletteBitmapWrapper resource, GlideAnimation<? super PaletteBitmapWrapper> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);

                        int color = PaletteUtils.getColorWithDefault(resource.getPalette(), getResources().getColor(R.color.primaryBlue500));
                    }
                });

    }
}

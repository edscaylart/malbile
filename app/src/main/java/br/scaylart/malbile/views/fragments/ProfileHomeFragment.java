package br.scaylart.malbile.views.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.presenters.ProfileHomePresenterImpl;
import br.scaylart.malbile.presenters.listeners.ProfileFragmentPresenter;
import br.scaylart.malbile.utils.DateTools;
import br.scaylart.malbile.utils.PaletteBitmapTarget;
import br.scaylart.malbile.utils.PaletteBitmapTranscoder;
import br.scaylart.malbile.utils.PaletteUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.PaletteBitmapWrapper;
import br.scaylart.malbile.views.activities.ProfileActivity;
import br.scaylart.malbile.views.listeners.ProfileFragmentView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileHomeFragment extends Fragment implements ProfileFragmentView {
    public static final String TAG = ProfileHomeFragment.class.getSimpleName();
    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    private ProfileFragmentPresenter mProfileFragmentPresenter;
    private View mView;

    /**
     * Profile Fields
     */
    @InjectView(R.id.profileImage) ImageView mImageCover;
    @InjectView(R.id.profileUsername) TextView mTextUsername;
    @InjectView(R.id.profileLastOnline) TextView mTextLastOnline;
    @InjectView(R.id.btnAnimeList) Button mBtnAnimeList;
    @InjectView(R.id.btnMangaList) Button mBtnMangaList;
    /**
     * Information Fields
     */
    @InjectView(R.id.genderText) TextView mTextGender;
    @InjectView(R.id.birthdayText) TextView mTextBirthday;
    @InjectView(R.id.locationText) TextView mTextLocation;
    @InjectView(R.id.websiteText) TextView mTextWebSite;
    @InjectView(R.id.joinDateText) TextView mTextJoinDate;
    @InjectView(R.id.accessRankText) TextView mTextAccessRank;
    @InjectView(R.id.animeListViewsText) TextView mTextAnimeViews;
    @InjectView(R.id.mangaListViewsText) TextView mTextMangaViews;
    @InjectView(R.id.commentsText) TextView mTextComments;
    @InjectView(R.id.forumPostsText) TextView mTextForum;
    /**
     * Anime Stats Fields
     */
    @InjectView(R.id.aTimeText) TextView mTextAnimeTime;
    @InjectView(R.id.watchingText) TextView mTextAnimeWatching;
    @InjectView(R.id.aCompletedText) TextView mTextAnimeCompleted;
    @InjectView(R.id.aOnHoldText) TextView mTextAnimeOnHold;
    @InjectView(R.id.aDroppedText) TextView mTextAnimeDropped;
    @InjectView(R.id.aPlannedText) TextView mTextAnimePlanned;
    @InjectView(R.id.aTotalEntriesText) TextView mTextAnimeTotal;
    @InjectView(R.id.aCompatibilityText) TextView mTextAnimeCompatibility;
    @InjectView(R.id.aProgressBarC) ProgressBar mProgressAnimeComp;
    @InjectView(R.id.aRelativeCompatibility) RelativeLayout mRelativeAnimeComp;
    /**
     * Manga Stats Fields
     */
    @InjectView(R.id.mTimeText) TextView mTextMangaTime;
    @InjectView(R.id.readingText) TextView mTextMangaReading;
    @InjectView(R.id.mCompletedText) TextView mTextMangaCompleted;
    @InjectView(R.id.mOnHoldText) TextView mTextMangaOnHold;
    @InjectView(R.id.mDroppedText) TextView mTextMangaDropped;
    @InjectView(R.id.mPlannedText) TextView mTextMangaPlanned;
    @InjectView(R.id.mTotalEntriesText) TextView mTextMangaTotal;
    @InjectView(R.id.mCompatibilityText) TextView mTextMangaCompatibility;
    @InjectView(R.id.mProgressBarC) ProgressBar mProgressMangaComp;
    @InjectView(R.id.mRelativeCompatibility) RelativeLayout mRelativeMangaComp;

    public static ProfileHomeFragment newInstance(String username) {
        ProfileHomeFragment newInstance = new ProfileHomeFragment();

        Bundle arguments = new Bundle();
        arguments.putString(USERNAME_ARGUMENT_KEY, username);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        mProfileFragmentPresenter = new ProfileHomePresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_profile_home, container, false);
        ButterKnife.inject(this, detailView);
        mView = detailView;

        mProfileFragmentPresenter.initializeViews();

        return detailView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mProfileFragmentPresenter.restoreState(savedInstanceState);
        } else {
            mProfileFragmentPresenter.handleInitialArguments(getArguments());
        }

        mProfileFragmentPresenter.initializeData();
    }

    @Override
    public void onStart() {
        super.onStart();

        mProfileFragmentPresenter.registerForEvents();
    }

    @Override
    public void onStop() {
        mProfileFragmentPresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mProfileFragmentPresenter.destroyAllSubscriptions();
        mProfileFragmentPresenter.releaseAllResources();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mProfileFragmentPresenter.saveState(outState);
    }

    @Override
    public ProfileActivity getContext() {
        return (ProfileActivity) getActivity();
    }

    @Override
    public void initializeViews() {

    }

    @OnClick(R.id.btnAnimeList)
    public void onListAnimeCLick(View v) {
        mProfileFragmentPresenter.openLibrary(BaseService.ListType.ANIME);
    }

    @OnClick(R.id.btnMangaList)
    public void onListMangaClick(View v) {
        mProfileFragmentPresenter.openLibrary(BaseService.ListType.MANGA);
    }

    public void setcolor() {
        User record = getContext().getUserRecord();

        String name = record.getUsername();
        String rank = record.getProfile().getDetails().getAccessRank() != null ? record.getProfile().getDetails().getAccessRank() : "";

        setColor(true);
        setColor(false);
        if (rank.contains("Administrator")) {
            mTextAccessRank.setTextColor(Color.parseColor("#850000"));
        } else if (rank.contains("Moderator")) {
            mTextAccessRank.setTextColor(Color.parseColor("#003385"));
        } else {
            mTextAccessRank.setTextColor(Color.parseColor("#0D8500")); //normal user
        }
        mTextWebSite.setTextColor(Color.parseColor("#002EAB"));

        if (User.isDeveloperRecord(name)) {
            mTextAccessRank.setText(R.string.access_rank_developer); //Developer
            mTextAccessRank.setTextColor(getResources().getColor(R.color.primaryBlue700)); //Developer
        }
    }

    public void setColor(boolean type) {
        User record = getContext().getUserRecord();
        int Hue;
        TextView textview;
        if (type) {
            textview = mTextAnimeTime; //anime
            Hue = (int) (record.getProfile().getAnimeStats().getTimeDays() * 2.5);
        } else {
            textview = mTextMangaTime; // manga
            Hue = (int) (record.getProfile().getMangaStats().getTimeDays() * 5);
        }
        if (Hue > 359) {
            Hue = 359;
        }
        textview.setTextColor(Color.HSVToColor(new float[]{Hue, 1, (float) 0.7}));
    }


    @Override
    public void initializeData(String username) {
        User record = getContext().getUserRecord();

        if (record != null) {
            setThumbnail(record.getProfile().getAvatarUrl());

            mTextUsername.setText(record.getUsername());
            if (record.getProfile().getDetails().getLastOnline() != null) {
                String lastOnline = DateTools.formatDateString(record.getProfile().getDetails().getLastOnline(), getContext(), true);
                mTextLastOnline.setText(lastOnline.equals("") ? record.getProfile().getDetails().getLastOnline() : lastOnline);
            } else {
                mTextLastOnline.setText("-");
            }

            if (!AccountService.getUsername().equals(username)) {
                mBtnAnimeList.setVisibility(View.VISIBLE);
                mBtnMangaList.setVisibility(View.VISIBLE);
            } else {
                mBtnAnimeList.setVisibility(View.GONE);
                mBtnMangaList.setVisibility(View.GONE);
            }

            mTextGender.setText(StringUtils.getStringFromResourceArray(R.array.profile_info_gender, R.string.profile_not_specified, record.getProfile().getDetails().getGenderInt()));
            if (record.getProfile().getDetails().getBirthday() != null) {
                String birthday = DateTools.formatDateString(record.getProfile().getDetails().getBirthday(), getContext(), true);
                mTextBirthday.setText(birthday.equals("") ? record.getProfile().getDetails().getBirthday() : birthday);
            } else {
                mTextBirthday.setText("-");
            }
            mTextLocation.setText(StringUtils.nullCheck(record.getProfile().getDetails().getLocation(), R.string.profile_not_specified));
            if (record.getProfile().getDetails().getWebsite() != null && record.getProfile().getDetails().getWebsite().contains("http://") && record.getProfile().getDetails().getWebsite().contains(".")) { // filter fake websites
                mTextWebSite.setText(record.getProfile().getDetails().getWebsite().replace("http://", ""));
            } else {
                mTextWebSite.setVisibility(View.GONE);
            }
            if (record.getProfile().getDetails().getJoinDate() != null) {
                String joinDate = DateTools.formatDateString(record.getProfile().getDetails().getJoinDate(), getContext(), true);
                mTextJoinDate.setText(joinDate.equals("") ? record.getProfile().getDetails().getJoinDate() : joinDate);
            } else {
                mTextJoinDate.setText("-");
            }
            mTextAccessRank.setText(StringUtils.nullCheck(record.getProfile().getDetails().getAccessRank(), R.string.profile_not_specified));
            mTextAnimeViews.setText(String.valueOf(record.getProfile().getDetails().getAnimeListView()));
            mTextMangaViews.setText(String.valueOf(record.getProfile().getDetails().getMangaListView()));
            mTextComments.setText(StringUtils.nullCheck(record.getProfile().getDetails().getComments()));
            mTextForum.setText(StringUtils.nullCheck(record.getProfile().getDetails().getForumPosts()));

            mTextAnimeTime.setText(String.valueOf(record.getProfile().getAnimeStats().getTimeDays()));
            mTextAnimeWatching.setText(String.valueOf(record.getProfile().getAnimeStats().getWatching()));
            mTextAnimeCompleted.setText(String.valueOf(record.getProfile().getAnimeStats().getCompleted()));
            mTextAnimeOnHold.setText(String.valueOf(record.getProfile().getAnimeStats().getOnHold()));
            mTextAnimeDropped.setText(String.valueOf(record.getProfile().getAnimeStats().getDropped()));
            mTextAnimePlanned.setText(String.valueOf(record.getProfile().getAnimeStats().getPlanToWatch()));
            mTextAnimeTotal.setText(String.valueOf(record.getProfile().getAnimeStats().getTotalEntries()));
            mRelativeAnimeComp.setVisibility(User.isDeveloperRecord(username) ? View.GONE : View.VISIBLE);

            mTextMangaTime.setText(String.valueOf(record.getProfile().getMangaStats().getTimeDays()));
            mTextMangaReading.setText(String.valueOf(record.getProfile().getMangaStats().getReading()));
            mTextMangaCompleted.setText(String.valueOf(record.getProfile().getMangaStats().getCompleted()));
            mTextMangaOnHold.setText(String.valueOf(record.getProfile().getMangaStats().getOnHold()));
            mTextMangaDropped.setText(String.valueOf(record.getProfile().getMangaStats().getDropped()));
            mTextMangaPlanned.setText(String.valueOf(record.getProfile().getMangaStats().getPlanToRead()));
            mTextMangaTotal.setText(String.valueOf(record.getProfile().getMangaStats().getTotalEntries()));
            mRelativeMangaComp.setVisibility(User.isDeveloperRecord(username) ? View.GONE : View.VISIBLE);

            if (!AccountService.getUsername().equals(username)) {
                mTextAnimeCompatibility.setText(StringUtils.getString(R.string.profile_info_compatibility).replace("$cpt", StringUtils.nullCheck(record.getProfile().getDetails().getAnimeCompatibility())));
                mTextMangaCompatibility.setText(StringUtils.getString(R.string.profile_info_compatibility).replace("$cpt", StringUtils.nullCheck(record.getProfile().getDetails().getMangaCompatibility())));
                if (record.getProfile().getDetails().getAnimeCompatibilityValue() != null
                        && !record.getProfile().getDetails().getAnimeCompatibilityValue().trim().equals("")
                        && record.getProfile().getDetails().getAnimeCompatibilityValue().contains(".")) {
                    float animeComp = Float.parseFloat(record.getProfile().getDetails().getAnimeCompatibilityValue().trim());
                    mProgressAnimeComp.setProgress(Math.round(animeComp));
                }
                if (record.getProfile().getDetails().getMangaCompatibilityValue() != null
                        && !record.getProfile().getDetails().getMangaCompatibilityValue().trim().equals("")
                        && record.getProfile().getDetails().getMangaCompatibilityValue().contains(".")) {
                    float mangaComp = Float.parseFloat(record.getProfile().getDetails().getMangaCompatibilityValue().trim());
                    mProgressMangaComp.setProgress(Math.round(mangaComp));
                }
            }

            setcolor();
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

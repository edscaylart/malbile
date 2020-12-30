package br.scaylart.malbile.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.controllers.networks.StatusUpdatedReceiver;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.presenters.DetailPresenterImpl;
import br.scaylart.malbile.presenters.listeners.DetailPresenter;
import br.scaylart.malbile.utils.PreferenceUtils;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.adapters.pager.DetailPagerAdapter;
import br.scaylart.malbile.views.dialogs.NumberPickerDialogFragment;
import br.scaylart.malbile.views.dialogs.RatingPickerDialogFragment;
import br.scaylart.malbile.views.dialogs.RemoveConfirmationDialogFragment;
import br.scaylart.malbile.views.fragments.DetailPersonalFragment;
import br.scaylart.malbile.views.listeners.DetailView;
import br.scaylart.malbile.views.widget.SlidingTabLayout;

public class DetailActivity extends BaseActivity implements DetailView, NumberPickerDialogFragment.onUpdateClickListener, RatingPickerDialogFragment.onUpdateClickListener, PopupMenu.OnMenuItemClickListener, Switch.OnCheckedChangeListener {
    public static final String TAG = DetailActivity.class.getSimpleName();

    public static final String PRESENTER_ARGUMENT_KEY = TAG + ":" + "PresenterArgumentKey";
    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestArgumentKey";

    private DetailPresenter mDetailPresenter;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;
    private RelativeLayout mEmptyRelativeLayout;
    private DetailPagerAdapter mDetailPagerAdapter;
    private ProgressDialog dialog;

    private RequestWrapper mRequest;

    private Menu menu;

    public Anime animeRecord;

    public Manga mangaRecord;

    public static Intent constructDetailActivityIntent(Context context, RequestWrapper request) {
        Intent argumentIntent = new Intent(context, DetailActivity.class);
        //argumentIntent.putExtra(PRESENTER_ARGUMENT_KEY, MangaPresenterOnlineImpl.TAG);
        argumentIntent.putExtra(REQUEST_ARGUMENT_KEY, request);

        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailPresenter = new DetailPresenterImpl(this);

        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);

        if (savedInstanceState != null) {
            mDetailPresenter.restoreState(savedInstanceState);
        } else {
            mDetailPresenter.handleInitialArguments(getIntent());
        }

        mDetailPresenter.initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        setMenu(mDetailPresenter.getRequestWrapper());
        return true;
    }

    @Override
    public void setMenu(RequestWrapper request) {
        if (menu != null) {
            if (isAdded(request)) {
                // TODO deixar visivel se houver NET
                menu.findItem(R.id.action_remove).setVisible(true);
            } else {
                menu.findItem(R.id.action_remove).setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_Share:
                Share();
                break;
            case R.id.action_remove:
                showDialog("removeConfirmation", new RemoveConfirmationDialogFragment());
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDetailPresenter.registerForEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDetailPresenter.onResume();
    }

    @Override
    protected void onStop() {
        mDetailPresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDetailPresenter.destroyAllSubscriptions();
        // mDetailPresenter.releaseAllResources();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mDetailPresenter.saveState(outState);
    }

    // DetailView

    @Override
    public void initializeViews(RequestWrapper request) {
        mRequest = request;

        mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), request);

        mViewPager.setAdapter(mDetailPagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentPinkA200);
            }
        });
        mTabs.setViewPager(mViewPager);
    }

    @Override
    public void setAnimeRecord(Anime record) {
        animeRecord = record;
    }

    @Override
    public void setMangaRecord(Manga record) {
        mangaRecord = record;
    }

    public Anime getAnimeRecord() {
        return animeRecord;
    }

    public Manga getMangaRecord() {
        return mangaRecord;
    }

    @Override
    public void setTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    private boolean isEmpty() {
        return animeRecord == null && mangaRecord == null;
    }

    @Override
    public boolean isAdded(RequestWrapper request) {
        return !isEmpty() && (request.getListType().equals(BaseService.ListType.ANIME) ? animeRecord.getMyStatus() != 0 : mangaRecord.getMyStatus() != 0);
    }

    @Override
    public void showDialog(String tag, DialogFragment dialog, Bundle args) {
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "fragment_" + tag);
    }

    @Override
    public void showDialog(String tag, DialogFragment dialog) {
        dialog.show(getSupportFragmentManager(), "fragment_" + tag);
    }

    @Override
    public void showProgressDialog(int titleString, int msgString) {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getString(titleString));
        dialog.setMessage(getString(msgString));
        dialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void initializeToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primaryBlue500));

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initializeEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setImageResource(R.drawable.ic_image_white_48dp);
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setColorFilter(getResources().getColor(R.color.accentPinkA200), PorterDuff.Mode.MULTIPLY);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText("No Anime/Manga");
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.instructionsTextView)).setText("Wait a sec");
        }
    }

    @Override
    public void hideEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyRelativeLayout() {

    }

    @Override
    public void updateInformation() {
        mDetailPresenter.updateInformations();
    }

    public void onDialogDismissed(boolean startDate, int year, int month, int day) {
        String monthString = Integer.toString(month);
        if (monthString.length() == 1)
            monthString = "0" + monthString;

        String dayString = Integer.toString(day);
        if (dayString.length() == 1)
            dayString = "0" + dayString;
        if (isAnime()) {
            if (startDate)
                animeRecord.setListStartDate(Integer.toString(year) + "-" + monthString + "-" + dayString);
            else
                animeRecord.setListFinishDate(Integer.toString(year) + "-" + monthString + "-" + dayString);
        } else {
            if (startDate)
                mangaRecord.setListStartDate(Integer.toString(year) + "-" + monthString + "-" + dayString);
            else
                mangaRecord.setListFinishDate(Integer.toString(year) + "-" + monthString + "-" + dayString);
        }

        ((DetailPersonalFragment) mDetailPagerAdapter.getLibrary(1)).initializeData(mDetailPresenter.getRequestWrapper());
    }

    @Override
    public void onUpdatedFromDialogPicker(int number, int id) {
        switch (id) {
            case R.id.personalProgress:
                if (isAnime()) {
                    if (number != animeRecord.getWatchedEpisodes()) {
                        animeRecord.setWatchedEpisodes(number);
                    }
                } else {
                    if (number != mangaRecord.getChaptersRead()) {
                        mangaRecord.setChaptersRead(number);
                    }
                }
                break;
            case R.id.personalVolume:
                if (number != mangaRecord.getVolumesRead()) {
                    mangaRecord.setVolumesRead(number);
                }
                break;
            case R.id.personalScore:
                if (isAnime()) {
                    if (number != animeRecord.getScore()) {
                        animeRecord.setScore(number);
                    }
                } else {
                    if (number != mangaRecord.getScore()) {
                        mangaRecord.setScore(number);
                    }
                }
                break;
            case R.id.personalRewatchingCount:
                if (isAnime()) {
                    if (number != animeRecord.getRewatchingCount()) {
                        animeRecord.setRewatchingCount(number);
                    }
                } else {
                    if (number != mangaRecord.getRereadingCount()) {
                        mangaRecord.setRereadingCount(number);
                    }
                }
                break;
        }
        ((DetailPersonalFragment) mDetailPagerAdapter.getLibrary(1)).initializeData(mDetailPresenter.getRequestWrapper());
    }

    private boolean isAnime() {
        return mDetailPresenter.getRequestWrapper().getListType().equals(BaseService.ListType.ANIME);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (isAnime()) {
            if (animeRecord.getMyStatus() == 0)
                animeRecord.setCreateFlag(true);
        } else {
            if (mangaRecord.getMyStatus() == 0)
                mangaRecord.setCreateFlag(true);
        }
        switch (menuItem.getItemId()) {
            case R.id.status_watching:
                animeRecord.setMyStatus(1);
                break;
            case R.id.status_reading:
                mangaRecord.setMyStatus(1);
                break;
            case R.id.status_completed:
                if (isAnime())
                    animeRecord.setMyStatus(2);
                else
                    mangaRecord.setMyStatus(2);
                break;
            case R.id.status_onhold:
                if (isAnime())
                    animeRecord.setMyStatus(3);
                else
                    mangaRecord.setMyStatus(3);
                break;
            case R.id.status_dropped:
                if (isAnime())
                    animeRecord.setMyStatus(4);
                else
                    mangaRecord.setMyStatus(4);
                break;
            case R.id.status_plantowatch:
                animeRecord.setMyStatus(6);
                break;
            case R.id.status_plantoread:
                mangaRecord.setMyStatus(6);
                break;
        }

        ((DetailPersonalFragment) mDetailPagerAdapter.getLibrary(1)).initializeData(mDetailPresenter.getRequestWrapper());

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isAnime()) {
            if (animeRecord.isRewatching() != isChecked)
                animeRecord.setRewatching(isChecked);
        } else {
            if (mangaRecord.isRereading() != isChecked)
                mangaRecord.setRereading(isChecked);
        }
    }

    @Override
    public void onUpdatedInformation() {
        Intent i = new Intent();
        i.setAction(StatusUpdatedReceiver.RECEIVER);
        i.putExtra("type", mDetailPresenter.getRequestWrapper().getListType());
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(i);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    public void Share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, makeShareText());
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    /*
     * Make the share text for the share dialog
     */
    public String makeShareText() {
        int recordID = isAnime() ? animeRecord.getId() : mangaRecord.getId();
        String shareText = PreferenceUtils.getCustomShareText();
        shareText = shareText.replace("$title;", mToolbar.getTitle());
        shareText = shareText.replace("$link;", "http://myanimelist.net/" + mDetailPresenter.getRequestWrapper().getListType().toString().toLowerCase(Locale.US) + "/" + Integer.toString(recordID));
        shareText = shareText + getResources().getString(R.string.custom_share_text_malbile);
        return shareText;
    }

    public void onRemoveConfirmed() {
        if (isAnime()) {
            animeRecord.setDeleteFlag(true);
        } else {
            mangaRecord.setDeleteFlag(true);
        }
        mDetailPresenter.deleteInformations();
    }
}

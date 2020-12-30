package br.scaylart.malbile.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.presenters.ProfilePresenterImpl;
import br.scaylart.malbile.presenters.listeners.ProfilePresenter;
import br.scaylart.malbile.utils.PreferenceUtils;
import br.scaylart.malbile.views.adapters.pager.ProfilePagerAdapter;
import br.scaylart.malbile.views.listeners.ProfileView;
import br.scaylart.malbile.views.widget.SlidingTabLayout;

public class ProfileActivity extends BaseActivity implements ProfileView {
    public static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    private ProfilePresenter mProfilePresenter;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;
    private RelativeLayout mEmptyRelativeLayout;
    private ProgressDialog dialog;
    private ProfilePagerAdapter mProfilePagerAdapter;

    private String username;
    private Menu menu;

    public User userRecord;

    public static Intent constructProfileActivityIntent(Context context, String username) {
        Intent argumentIntent = new Intent(context, ProfileActivity.class);
        argumentIntent.putExtra(USERNAME_ARGUMENT_KEY, username);

        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfilePresenter = new ProfilePresenterImpl(this);

        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);

        if (savedInstanceState != null) {
            mProfilePresenter.restoreState(savedInstanceState);
        } else {
            mProfilePresenter.handleInitialArguments(getIntent());
        }

        mProfilePresenter.initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        setMenu();
        return true;
    }

    @Override
    public void setMenu() {
        if (menu != null) {
            if (username != null || (userRecord != null && userRecord.getUsername() != null)) {
                String user = username != null ? username : userRecord.getUsername();
                if (!user.equals(AccountService.getUsername())) {
                    menu.findItem(R.id.action_write_message).setVisible(true);
                } else {
                    menu.findItem(R.id.action_write_message).setVisible(false);
                }
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
            case R.id.action_write_message:
                writeMessage();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mProfilePresenter.registerForEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mProfilePresenter.onResume();
    }

    @Override
    protected void onStop() {
        mProfilePresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mProfilePresenter.destroyAllSubscriptions();
        // mDetailPresenter.releaseAllResources();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mProfilePresenter.saveState(outState);
    }

    // ProfileView:

    @Override
    public void initializeViews(String username) {
        this.username = username;

        mProfilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), userRecord.getUsername());

        mViewPager.setAdapter(mProfilePagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentPinkA200);
            }
        });
        mTabs.setViewPager(mViewPager);

        if (getIntent().getExtras().containsKey("friends")) {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public void setUserRecord(User record) {
        userRecord = record;
    }

    @Override
    public User getUserRecord() {
        return userRecord;
    }

    @Override
    public void setTitle(String user) {
        if (mToolbar != null) {
            mToolbar.setTitle(getResources().getString(R.string.fragment_profile).replace("$user", user));
        }
    }

    @Override
    public boolean isFriend() {
        return false;
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
    public void closeActivity() {
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void initializeEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setImageResource(R.drawable.ic_image_white_48dp);
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setColorFilter(getResources().getColor(R.color.accentPinkA200), PorterDuff.Mode.MULTIPLY);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText("No Profile");
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
    public void initializeToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primaryBlue500));

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        String shareText = PreferenceUtils.getCustomShareText();
        shareText = shareText.replace("$title;", mToolbar.getTitle());
        shareText = shareText.replace("$link;", "http://myanimelist.net/profile/" + userRecord.getUsername());
        shareText = shareText + getResources().getString(R.string.custom_share_text_malbile);
        return shareText;
    }

    public void writeMessage() {
        Intent msgIntent = SendMessageActivity.constructSendMessageActivityIntent(getContext(), userRecord.getUsername());
        getContext().startActivity(msgIntent);
    }
}

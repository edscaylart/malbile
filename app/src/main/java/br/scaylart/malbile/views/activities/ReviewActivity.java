package br.scaylart.malbile.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.presenters.ReviewPresenterImpl;
import br.scaylart.malbile.presenters.listeners.ReviewPresenter;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.dialogs.RatingPickerDialogFragment;
import br.scaylart.malbile.views.listeners.ReviewView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ReviewActivity extends BaseActivity implements ReviewView, RatingPickerDialogFragment.onUpdateClickListener {
    public static final String TAG = ReviewActivity.class.getSimpleName();
    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestParcelableKey";
    public static final String PROGRESS_ARGUMENT_KEY = TAG + ":" + "ProgressParcelableKey";
    public static final String TITLE_ARGUMENT_KEY = TAG + ":" + "TitleParcelableKey";

    private Toolbar mToolbar;
    private RequestWrapper mRequest;
    private int mProgress;
    private String mTitle;

    private ReviewPresenter mReviewPresenter;

    ProgressDialog dialog;

    @InjectView(R.id.revStory) RatingBar mRevStory;
    @InjectView(R.id.revEnjoyment) RatingBar mRevEnjoyment;
    @InjectView(R.id.lbArt) TextView mLabelArt;
    @InjectView(R.id.revArt) RatingBar mRevArt;
    @InjectView(R.id.revCharacter) RatingBar mRevCharacter;
    @InjectView(R.id.revSound) RatingBar mRevSound;
    @InjectView(R.id.revOverall) RatingBar mRevOverall;
    @InjectView(R.id.message_msg) EditText mMessage;
    @InjectView(R.id.relativeSound) RelativeLayout mRelativeSound;

    public static Intent constructReviewActivityIntent(Context context, RequestWrapper request, int progress, String title) {
        Intent argumentIntent = new Intent(context, ReviewActivity.class);
        argumentIntent.putExtra(REQUEST_ARGUMENT_KEY, request);
        argumentIntent.putExtra(PROGRESS_ARGUMENT_KEY, progress);
        argumentIntent.putExtra(TITLE_ARGUMENT_KEY, title);

        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReviewPresenter = new ReviewPresenterImpl(this);

        setContentView(R.layout.activity_review);
        ButterKnife.inject(this);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        if (savedInstanceState != null) {
            mReviewPresenter.restoreState(savedInstanceState);
        } else {
            mReviewPresenter.handleInitialArguments(getIntent());
        }

        mReviewPresenter.initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_review_send:
                sendReview();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mReviewPresenter.registerForEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mReviewPresenter.onResume();
    }

    @Override
    protected void onStop() {
        mReviewPresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mReviewPresenter.destroyAllSubscriptions();
        // mDetailPresenter.releaseAllResources();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mReviewPresenter.saveState(outState);
    }

    @Override
    public void initializeViews(RequestWrapper request, int progress, String title) {
        mRequest = request;
        mProgress = progress;
        mTitle = title;

        mLabelArt.setText(isAnime() ? "Animation" : "Art");
        mRelativeSound.setVisibility(isAnime() ? View.VISIBLE : View.GONE);
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
    public void initializeToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primaryBlue500));

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void sendReview() {
        //if (isValid()) {
        showProgressDialog(R.string.dialog_title_update_info, R.string.dialog_msg_update_info);

        String story = String.valueOf((int) ((float) Math.ceil(mRevStory.getRating() * (float) 2.0)));
        String enjoymenty = String.valueOf((int) ((float) Math.ceil(mRevEnjoyment.getRating() * (float) 2.0)));
        String art = String.valueOf((int) ((float) Math.ceil(mRevArt.getRating() * (float) 2.0)));
        String character = String.valueOf((int) ((float) Math.ceil(mRevCharacter.getRating() * (float) 2.0)));
        String sound = String.valueOf((int) ((float) Math.ceil(mRevSound.getRating() * (float) 2.0)));
        String overall = String.valueOf((int) ((float) Math.ceil(mRevOverall.getRating() * (float) 2.0)));
        String message = mMessage.getText().toString().trim();

        HashMap<String, String> nameValuePairList = new HashMap<>();
        nameValuePairList.put(isAnime() ? "episodes" : "chapters", String.valueOf(mProgress));
        nameValuePairList.put("message", message);
        nameValuePairList.put("story", story);
        nameValuePairList.put("art", art);
        nameValuePairList.put("sound", isAnime() ? sound : "");
        nameValuePairList.put("character", character);
        nameValuePairList.put("overall", overall);
        nameValuePairList.put("enjoymenty", enjoymenty);
        nameValuePairList.put("id", String.valueOf(mRequest.getId()));
        nameValuePairList.put("title", mTitle);

        mReviewPresenter.sendReview(nameValuePairList);
        //}
    }

    public boolean isAnime() {
        return mRequest.getListType().equals(BaseService.ListType.ANIME);
    }

    public void showProgressDialog(int titleString, int msgString) {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getString(titleString));
        dialog.setMessage(getString(msgString));
        dialog.show();
    }

    public void showDialog(String tag, DialogFragment dialog, Bundle args) {
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "fragment_" + tag);
    }

    @Override
    public void onUpdatedFromDialogPicker(int number, int id) {
        ((RatingBar) findViewById(id)).setRating((float) number / 2);
    }

    @OnClick({R.id.relativeStory, R.id.relativeArt, R.id.relativeEnjoyment, R.id.relativeCharacter, R.id.relativeSound, R.id.relativeOverall})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeStory:
                Bundle argsSt = new Bundle();
                argsSt.putInt("id", R.id.revStory);
                argsSt.putInt("current", (int) ((float) Math.ceil(mRevStory.getRating() * (float) 2.0)));
                argsSt.putString("title", "Story");
                showDialog("review", new RatingPickerDialogFragment().setOnSendClickListener(this), argsSt);
                break;
            case R.id.relativeArt:
                Bundle argsAt = new Bundle();
                argsAt.putInt("id", R.id.revArt);
                argsAt.putInt("current", (int) ((float) Math.ceil(mRevArt.getRating() * (float) 2.0)));
                argsAt.putString("title", mLabelArt.getText().toString());
                showDialog("review", new RatingPickerDialogFragment().setOnSendClickListener(this), argsAt);
                break;
            case R.id.relativeEnjoyment:
                Bundle argsEj = new Bundle();
                argsEj.putInt("id", R.id.revEnjoyment);
                argsEj.putInt("current", (int) ((float) Math.ceil(mRevEnjoyment.getRating() * (float) 2.0)));
                argsEj.putString("title", "Enjoyment");
                showDialog("review", new RatingPickerDialogFragment().setOnSendClickListener(this), argsEj);
                break;
            case R.id.relativeCharacter:
                Bundle argsCh = new Bundle();
                argsCh.putInt("id", R.id.revCharacter);
                argsCh.putInt("current", (int) ((float) Math.ceil(mRevCharacter.getRating() * (float) 2.0)));
                argsCh.putString("title", "Character");
                showDialog("review", new RatingPickerDialogFragment().setOnSendClickListener(this), argsCh);
                break;
            case R.id.relativeSound:
                Bundle argsSd = new Bundle();
                argsSd.putInt("id", R.id.revSound);
                argsSd.putInt("current", (int) ((float) Math.ceil(mRevSound.getRating() * (float) 2.0)));
                argsSd.putString("title", "Sound");
                showDialog("review", new RatingPickerDialogFragment().setOnSendClickListener(this), argsSd);
                break;
            case R.id.relativeOverall:
                Bundle argsOv = new Bundle();
                argsOv.putInt("id", R.id.revOverall);
                argsOv.putInt("current", (int) ((float) Math.ceil(mRevOverall.getRating() * (float) 2.0)));
                argsOv.putString("title", "Overall");
                showDialog("review", new RatingPickerDialogFragment().setOnSendClickListener(this), argsOv);
                break;
        }
    }
}

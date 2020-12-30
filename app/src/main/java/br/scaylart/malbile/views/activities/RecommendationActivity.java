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
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.presenters.RecommendationPresenterImpl;
import br.scaylart.malbile.presenters.listeners.RecommendationPresenter;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.dialogs.SimilarDialogFragment;
import br.scaylart.malbile.views.listeners.RecommendationView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecommendationActivity extends BaseActivity implements RecommendationView, SimilarDialogFragment.onListClickListener {
    public static final String TAG = RecommendationActivity.class.getSimpleName();
    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestParcelableKey";
    public static final String TITLE_ARGUMENT_KEY = TAG + ":" + "TitleParcelableKey";

    private Toolbar mToolbar;
    private RequestWrapper mRequest;
    private String mTitle;
    private BaseRecord mSimilarRecord;

    private RecommendationPresenter mRecommendationPresenter;

    ProgressDialog dialog;

    @InjectView(R.id.recSeries) TextView mSeries;
    @InjectView(R.id.recMessage) EditText mMessage;

    public static Intent constructRecommendationActivityIntent(Context context, RequestWrapper request, String title) {
        Intent argumentIntent = new Intent(context, RecommendationActivity.class);
        argumentIntent.putExtra(REQUEST_ARGUMENT_KEY, request);
        argumentIntent.putExtra(TITLE_ARGUMENT_KEY, title);

        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecommendationPresenter = new RecommendationPresenterImpl(this);

        setContentView(R.layout.activity_recommendation);
        ButterKnife.inject(this);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        if (savedInstanceState != null) {
            mRecommendationPresenter.restoreState(savedInstanceState);
        } else {
            mRecommendationPresenter.handleInitialArguments(getIntent());
        }

        mRecommendationPresenter.initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommendation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_rec_send:
                sendRecommendation();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRecommendationPresenter.registerForEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRecommendationPresenter.onResume();
    }

    @Override
    protected void onStop() {
        mRecommendationPresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRecommendationPresenter.destroyAllSubscriptions();
        // mDetailPresenter.releaseAllResources();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecommendationPresenter.saveState(outState);
    }

    @Override
    public void initializeViews(RequestWrapper request, String title) {
        mRequest = request;
        mTitle = title;

        mSeries.setText(isAnime() ? "Choose a similar anime title" : "Choose a similar manga title");
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
    public void setTitle(String title) {
        mToolbar.setTitle(title);
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

    private void sendRecommendation() {
        showProgressDialog(R.string.dialog_title_update_info, R.string.dialog_msg_update_info);

        /*List<NameValuePair> nameValuePairList = new ArrayList<>(3);
        nameValuePairList.add(new BasicNameValuePair("rec_text[]", mMessage.getText().toString().trim()));
        nameValuePairList.add(new BasicNameValuePair("rec[]", String.valueOf(mSimilarRecord.getId())));
        nameValuePairList.add(new BasicNameValuePair("subrec", "Submit+My+Recommendations"));*/

        HashMap<String, String> nameValuePairList = new HashMap<>();
        nameValuePairList.put("similar", String.valueOf(mSimilarRecord.getId()));
        nameValuePairList.put("message", mMessage.getText().toString().trim());

        mRecommendationPresenter.sendRecommendation(nameValuePairList);
    }

    @OnClick(R.id.relativeSeries)
    public void onSimilarClick(View v) {
        Bundle args = new Bundle();
        args.putParcelable("request", mRequest);
        showDialog("similar", new SimilarDialogFragment().setOnSendClickListener(this), args);
    }

    @Override
    public void onSelectSimilarDialogPicker(BaseRecord record) {
        mSimilarRecord = record;
        mSeries.setText(record.getTitle());
    }
}

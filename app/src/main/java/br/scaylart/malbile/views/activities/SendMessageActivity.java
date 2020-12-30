package br.scaylart.malbile.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.R;
import br.scaylart.malbile.presenters.SendMessagePresenterImpl;
import br.scaylart.malbile.presenters.listeners.MessagePresenter;
import br.scaylart.malbile.views.listeners.MessageView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SendMessageActivity extends BaseActivity implements MessageView {
    public static final String TAG = SendMessageActivity.class.getSimpleName();
    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";

    private Toolbar mToolbar;
    private String username;

    private MessagePresenter mMessagePresenter;
    ProgressDialog dialog;

    @InjectView(R.id.toUsername) TextView mTextUsername;
    @InjectView(R.id.message_subject) TextView mTextSubject;
    @InjectView(R.id.message_msg) TextView mTextMessage;

    public static Intent constructSendMessageActivityIntent(Context context, String username) {
        Intent argumentIntent = new Intent(context, SendMessageActivity.class);
        argumentIntent.putExtra(USERNAME_ARGUMENT_KEY, username);

        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessagePresenter = new SendMessagePresenterImpl(this);

        setContentView(R.layout.activity_send_message);
        ButterKnife.inject(this);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        if (savedInstanceState != null) {
            mMessagePresenter.restoreState(savedInstanceState);
        } else {
            mMessagePresenter.handleInitialArguments(getIntent());
        }

        mMessagePresenter.initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_message_send:
                sendMessage();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMessagePresenter.registerForEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMessagePresenter.onResume();
    }

    @Override
    protected void onStop() {
        mMessagePresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMessagePresenter.destroyAllSubscriptions();
        // mDetailPresenter.releaseAllResources();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mMessagePresenter.saveState(outState);
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

    public boolean isValid() {
        return !mTextMessage.getText().equals("");
    }

    public void sendMessage() {
        if (isValid()) {
            showProgressDialog(R.string.dialog_title_update_info, R.string.dialog_msg_update_info);

            /*List<NameValuePair> nameValuePairList = new ArrayList<>(3);
            nameValuePairList.add(new BasicNameValuePair("subject", mTextSubject.getText().toString()));
            nameValuePairList.add(new BasicNameValuePair("message", mTextMessage.getText().toString() + "\n\n\nMsg sent by Malbile App"));
            nameValuePairList.add(new BasicNameValuePair("sendmessage", "Sending..."));*/

            mMessagePresenter.sendMessage(mTextSubject.getText().toString(), mTextMessage.getText().toString());
        }
    }

    public void showProgressDialog(int titleString, int msgString) {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getString(titleString));
        dialog.setMessage(getString(msgString));
        dialog.show();
    }

    @Override
    public void initializeViews(String username) {
        this.username = username;
        mTextUsername.setText(username);
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
}

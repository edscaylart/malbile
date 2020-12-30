package br.scaylart.malbile.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.scaylart.malbile.R;
import br.scaylart.malbile.presenters.UserLibraryPresenterImpl;
import br.scaylart.malbile.presenters.listeners.UserLibraryPresenter;
import br.scaylart.malbile.views.listeners.UserLibraryView;

public class LibraryActivity extends BaseActivity implements UserLibraryView {
    public static final String TAG = LibraryActivity.class.getSimpleName();
    public static final String REQUEST_ARGUMENT_KEY = TAG + ":" + "RequestParcelableKey";

    private Toolbar mToolbar;

    private UserLibraryPresenter mUserLibraryPresenter;

    public static Intent constructLibraryActivityIntent(Context context, Bundle data) {
        Intent argumentIntent = new Intent(context, LibraryActivity.class);
        argumentIntent.putExtra(REQUEST_ARGUMENT_KEY, data);
        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLibraryPresenter = new UserLibraryPresenterImpl(this);

        setContentView(R.layout.activity_library);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        if (savedInstanceState != null) {
            mUserLibraryPresenter.restoreState(savedInstanceState);
        } else {
            mUserLibraryPresenter.handleInitialArguments(getIntent().getBundleExtra(REQUEST_ARGUMENT_KEY));
        }

        mUserLibraryPresenter.initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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
}

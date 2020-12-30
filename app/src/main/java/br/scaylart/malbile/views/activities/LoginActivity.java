package br.scaylart.malbile.views.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.networks.PostService;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.utils.PreferenceUtils;
import br.scaylart.malbile.views.fragments.InitialFragment;
import br.scaylart.malbile.views.fragments.LoginFragment;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends ActionBarActivity implements LoginFragment.OnFragmentInteractionListener, InitialFragment.OnFragmentInteractionListener {
    public static final String TAG = LoginActivity.class.getSimpleName();

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    private Subscription mLoginSubscription;
    private ProgressDialog dialog;

    public boolean isLogged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPagerAdapter = new PagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changePagePosition(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    @Override
    public void showProgressDialog() {
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getString(R.string.dialog_title_verifying));
        dialog.setMessage(getString(R.string.dialog_message_verifying));
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void doLogin(final String username, final String password) {
        mLoginSubscription = MalbileManager.isAuthenticated(username, password)
                /*.flatMap(new Func1<User, Observable<HttpResponse>>() {
                    @Override
                    public Observable<HttpResponse> call(User user) {
                        if (user != null) {
                            isLogged = true;

                            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(5);
                            nameValuePairList.add(new BasicNameValuePair("user_name", username));
                            nameValuePairList.add(new BasicNameValuePair("password", password));
                            nameValuePairList.add(new BasicNameValuePair("cookie", "1"));
                            nameValuePairList.add(new BasicNameValuePair("sublogin", "Login"));
                            nameValuePairList.add(new BasicNameValuePair("csrf_token", PreferenceUtils.getTokenAcess()));
                            nameValuePairList.add(new BasicNameValuePair("submit", "1"));

                            return PostService.getTemporaryInstance()
                                    .executePost(PostService.BASE_HOST + "/login.php", nameValuePairList, null);
                        } else
                            return null;
                    }
                })
                .flatMap(new Func1<HttpResponse, Observable<String>>() {
                    @Override
                    public Observable<String> call(HttpResponse httpResponse) {
                        return PostService.mapCookieToString(PostService.BASE_HOST);
                    }
                })*/
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            hideProgressDialog();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        hideProgressDialog();
                        if (user != null) {
                            isLogged = true;
                            AccountService.addAccount(username, password);

                            Intent startMain = new Intent(LoginActivity.this, MainActivity.class);
                            startMain.putExtra(MainActivity.POSITION_ARGUMENT_KEY, PreferenceUtils.getStartupScreen());
                            startActivity(startMain);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoginSubscription != null) {
            mLoginSubscription.unsubscribe();
            mLoginSubscription = null;
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return InitialFragment.newInstance();
                case 1:
                    return LoginFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

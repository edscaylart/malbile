package br.scaylart.malbile.controllers.networks;

import android.webkit.CookieManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.utils.PreferenceUtils;
import rx.Observable;
import rx.Subscriber;

public class PostService extends BaseService {
    private static PostService sInstance;
    private static DefaultHttpClient mClient;

    private PostService() {
        mClient = new DefaultHttpClient();
    }

    public static PostService getPermanentInstance() {
        if (sInstance == null) {
            sInstance = new PostService();
        }

        return sInstance;
    }

    public static PostService getTemporaryInstance() {
        return new PostService();
    }

    public Observable<HttpResponse> executePost(final String url, final List<NameValuePair> formDataValues, final String cookie) {
        return Observable.create(new Observable.OnSubscribe<HttpResponse>() {
            @Override
            public void call(Subscriber<? super HttpResponse> subscriber) {
                try {
                    mClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                            "api-indiv-0A82E1C01531EA0C7E8349A8B82803BA");

                    if (AccountService.getAccount() != null) {
                        mClient.getCredentialsProvider().setCredentials(
                                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                                new UsernamePasswordCredentials(AccountService.getUsername(), AccountService.getPassword()));
                    }

                    HttpPost httppost = new HttpPost(url);

                    // Se houver cookie, passe para efetuar o post
                    if (cookie != null) {
                        httppost.setHeader("Cookie", cookie);
                    }

                    httppost.setEntity(new UrlEncodedFormEntity(formDataValues));

                    subscriber.onNext(mClient.execute(httppost));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<String> mapCookieToString(final String domainUrl) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    List<Cookie> cookies = mClient.getCookieStore().getCookies();
                    List<String> sessionCookie = new ArrayList<>();

                    for (int i = 0; i < cookies.size(); i++) {
                        Cookie cookie = cookies.get(i);
                        if (i==0)
                            sessionCookie.add("is_logged_in=1; path=" + cookie.getPath() + "; domain=" + cookie.getDomain());
                        sessionCookie.add(cookie.getName() + "=" + cookie.getValue() + "; path=" + cookie.getPath() + "; domain=" + cookie.getDomain());
                    }

                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setAcceptCookie(true);
                    cookieManager.removeSessionCookie();

                    for (int i = 0; i < sessionCookie.size(); i++) {
                        cookieManager.setCookie(domainUrl, sessionCookie.get(i));
                    }
                    PreferenceUtils.setCookie(cookieManager.getCookie(domainUrl));

                    subscriber.onNext(cookieManager.getCookie(domainUrl));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}

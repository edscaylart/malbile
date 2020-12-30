package br.scaylart.malbile.controllers.networks;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import br.scaylart.malbile.utils.PreferenceUtils;
import rx.Observable;
import rx.Subscriber;

public class ClientService extends BaseService {
    public static final int CONNECT_TIMEOUT = 100;
    public static final int WRITE_TIMEOUT = 100;
    public static final int READ_TIMEOUT = 300;

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "sessionid";

    private static ClientService sInstance;

    private OkHttpClient mClient;

    public enum ListType {
        ANIME,
        MANGA
    }

    private ClientService() {
        mClient = new OkHttpClient();
        mClient.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        mClient.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        mClient.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        //mClient.interceptors().add(new AddCookiesInterceptor());

    }

    public static ClientService getPermanentInstance() {
        if (sInstance == null) {
            sInstance = new ClientService();
        }

        return sInstance;
    }

    public static ClientService getTemporaryInstance() {
        return new ClientService();
    }

    public Observable<Response> getResponse(final String url) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .header("User-Agent", USER_AGENT)
                            .build();

                    subscriber.onNext(mClient.newCall(request).execute());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Response> getCustomResponse(final String url, final Headers headers) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .build();

                    subscriber.onNext(mClient.newCall(request).execute());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<String> mapResponseToString(final Response response) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * This interceptor put all the Cookies in Preferences in the Request.
     * Your implementation on how to get the Preferences MAY VARY.
     * <p/>
     * Created by tsuharesu on 4/1/15.
     */
    public class AddCookiesInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            String cookie = PreferenceUtils.getCookie();
            builder.addHeader("Cookie", cookie);

            return chain.proceed(builder.build());
        }
    }

}

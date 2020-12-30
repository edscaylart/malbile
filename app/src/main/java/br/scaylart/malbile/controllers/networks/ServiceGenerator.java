package br.scaylart.malbile.controllers.networks;

import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.mobprofs.retrofit.converters.SimpleXmlConverter;
import com.squareup.okhttp.OkHttpClient;

import br.scaylart.malbile.utils.errors.CustomErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ServiceGenerator {
    public static <S> S createService(Class<S> serviceClass, String baseUrl, String userAgent, String converterType) {
        return createService(serviceClass, baseUrl, userAgent, converterType, null, null, null);
    }

    /**
     * <h1>Cria o servico para acesso REST com Retrofit</h1>
     * @param serviceClass Classe Interface
     * @param baseUrl
     * @param converterType {json;xml}
     * @param username
     * @param password
     * @param cookie
     * @param <S>
     * @return
     */
    public static <S> S createService(Class<S> serviceClass, String baseUrl, final String userAgent, String converterType,
                                      final String username, final String password, final String cookie) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setErrorHandler(new CustomErrorHandler());

        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", userAgent);

                // Adiciona credencial case precise
                if (username != null && password != null) {
                    String credentials = "Basic " +
                            Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
                    request.addHeader("Authorization", credentials);
                }

                // Adicionar cookie caso precise
                if (cookie != null) {
                    request.addHeader("Cookie", cookie);
                }
            }
        };
        builder.setRequestInterceptor(interceptor);


        if (converterType.equals("json")) { // Se o resultado deve ser retornado em JSON
            builder.setConverter(new GsonConverter(new GsonBuilder().create()));
        } else if (converterType.equals("xml")) { // Se o resultado deve ser retornado em XML
            builder.setConverter(new SimpleXmlConverter());
        }

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }
}

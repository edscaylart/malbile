package br.scaylart.malbile.controllers.networks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import java.util.ArrayList;
import java.util.HashMap;

import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.networks.interfaces.MalApi;
import br.scaylart.malbile.models.Message;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Ed on 01/11/2015.
 */
public class RestApiService extends BaseService {
    public static final String BASE_HOST = "http://www.edsonsouza.eti.br/api/malbile/web/2";

    private MalApi service;

    private String username;

    public RestApiService() {
        username = AccountService.getUsername();
        setupRESTService(username, AccountService.getPassword());
    }

    public RestApiService(String username, String password) {
        this.username = username;
        setupRESTService(username, password);

    }

    private void setupRESTService(String username, String password) {
        /*service = ServiceGenerator.createService(MalApi.class,
                BASE_HOST,
                USER_AGENT,
                "xml",
                username,
                password,
                null);*/

        DefaultHttpClient client = new DefaultHttpClient();
        HttpProtocolParams.setUserAgent(client.getParams(), USER_AGENT);
        client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new ApacheClient(client))
                .setEndpoint(BASE_HOST)
                .setConverter(new GsonConverter(gson))
                .build();
        service = restAdapter.create(MalApi.class);
    }

    public ArrayList<Message> getUserMessages() {
        return service.getMessages().getList();
    }

    public boolean sendMessage(String username, String subject, String msg) {
        return service.sendMessage(username,subject, msg).getStatus() == 200;
    }

    public boolean postReviews(String type, HashMap<String, String> fieldMap) {
        return service.postReviews(type, fieldMap).getStatus() == 200;
    }

    public boolean postRecommendations(String type, HashMap<String, String> fieldMap) {
        return service.postRecommendations(type, fieldMap).getStatus() == 200;
    }
}

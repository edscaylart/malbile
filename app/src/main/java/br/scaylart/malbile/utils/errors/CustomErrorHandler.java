package br.scaylart.malbile.utils.errors;

import java.net.HttpURLConnection;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError error) {
        Response response = error.getResponse();
        if (response != null && response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return new Exception(error);
        }
        return error;
    }
}
package com.example.mobileapp.data.remote;

import com.example.mobileapp.MainApplication;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Эмулятор үшін localhost-тың IP адресі
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    private static volatile ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    // Add the AuthInterceptor to add the token to every request
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(MainApplication.getContext()))
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient) // Use the client with the interceptor
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    apiService = retrofit.create(ApiService.class);
                }
            }
        }
        return apiService;
    }


    public static void resetApiService() {
        apiService = null;
    }
}

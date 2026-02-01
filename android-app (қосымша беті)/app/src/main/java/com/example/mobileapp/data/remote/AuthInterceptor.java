package com.example.mobileapp.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobileapp.MainApplication;
import com.example.mobileapp.data.repository.AuthRepository;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AuthRepository.PREF_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(AuthRepository.KEY_TOKEN, null);

        Request originalRequest = chain.request();

        if (token != null) {
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}

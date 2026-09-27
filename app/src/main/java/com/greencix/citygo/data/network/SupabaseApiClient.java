package com.greencix.citygo.data.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greencix.citygo.data.local.SessionManager;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SupabaseApiClient {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static SupabaseApiClient instance;

    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;

    private SupabaseApiClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        this.gson = new GsonBuilder().create();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SupabaseApiClient(context);
        }
    }

    public static synchronized SupabaseApiClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SupabaseApiClient no está inicializado. Llama a init() primero.");
        }
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    private Headers buildHeaders(boolean useUserAuth) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("apikey", SupabaseConfig.SUPABASE_ANON_KEY);
        builder.add("Content-Type", "application/json");
        builder.add("Prefer", "return=representation");

        String token = SupabaseConfig.SUPABASE_ANON_KEY;
        if (useUserAuth) {
            try {
                String userToken = SessionManager.getInstance().getAccessToken();
                if (userToken != null && !userToken.isEmpty()) {
                    token = userToken;
                }
            } catch (Exception ignored) {}
        }
        builder.add("Authorization", "Bearer " + token);
        return builder.build();
    }

    public void post(String url, String jsonBody, boolean useUserAuth, Callback<String> callback) {
        RequestBody body = RequestBody.create(jsonBody != null ? jsonBody : "{}", JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(useUserAuth))
                .post(body)
                .build();
        executeCall(request, callback);
    }

    public void get(String url, boolean useUserAuth, Callback<String> callback) {
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(useUserAuth))
                .get()
                .build();
        executeCall(request, callback);
    }

    public void patch(String url, String jsonBody, boolean useUserAuth, Callback<String> callback) {
        RequestBody body = RequestBody.create(jsonBody != null ? jsonBody : "{}", JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(useUserAuth))
                .patch(body)
                .build();
        executeCall(request, callback);
    }

    public void rpc(String functionName, String jsonParams, boolean useUserAuth, Callback<String> callback) {
        String url = SupabaseConfig.RPC_BASE + functionName;
        post(url, jsonParams, useUserAuth, callback);
    }

    private void executeCall(Request request, Callback<String> callback) {
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError("Error de conexión: " + e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body() != null ? response.body().string() : "";
                final boolean isSuccessful = response.isSuccessful();
                final int code = response.code();

                mainHandler.post(() -> {
                    if (callback != null) {
                        if (isSuccessful) {
                            callback.onSuccess(responseBody);
                        } else {
                            callback.onError("Error en la solicitud (" + code + "): " + responseBody);
                        }
                    }
                });
            }
        });
    }
}

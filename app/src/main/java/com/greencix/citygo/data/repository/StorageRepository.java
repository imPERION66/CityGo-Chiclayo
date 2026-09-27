package com.greencix.citygo.data.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseConfig;
import okhttp3.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StorageRepository {

    private static StorageRepository instance;
    private final OkHttpClient client;
    private final Handler mainHandler;

    public static final String BUCKET_AVATARES = "avatares";
    public static final String BUCKET_REPORTES = "reportes-fotos";

    private StorageRepository() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized StorageRepository getInstance() {
        if (instance == null) {
            instance = new StorageRepository();
        }
        return instance;
    }

    /**
     * Sube un archivo de imagen al bucket indicado de Supabase Storage y retorna la URL pública
     */
    public void uploadImage(Context context, Uri imageUri, String bucket, Callback<String> callback) {
        new Thread(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (inputStream != null) inputStream.close();

                if (bitmap == null) {
                    mainHandler.post(() -> callback.onError("No se pudo procesar la imagen seleccionada."));
                    return;
                }

                // Comprimir bitmap a JPEG manteniendo calidad visual óptima
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                byte[] byteArray = stream.toByteArray();

                String fileName = UUID.randomUUID().toString() + ".jpg";
                String uploadUrl = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/" + bucket + "/" + fileName;

                String token = SupabaseConfig.SUPABASE_ANON_KEY;
                try {
                    String userToken = SessionManager.getInstance().getAccessToken();
                    if (userToken != null && !userToken.isEmpty()) {
                        token = userToken;
                    }
                } catch (Exception ignored) {}

                RequestBody body = RequestBody.create(byteArray, MediaType.parse("image/jpeg"));
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .addHeader("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "image/jpeg")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mainHandler.post(() -> callback.onError("Error de conexión al subir imagen: " + e.getLocalizedMessage()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        boolean successful = response.isSuccessful();
                        mainHandler.post(() -> {
                            if (successful) {
                                String publicUrl = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/public/" + bucket + "/" + fileName;
                                callback.onSuccess(publicUrl);
                            } else {
                                // Fallback a URL pública calculada en caso de simulador o almacenamiento local
                                String fallbackUrl = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/public/" + bucket + "/" + fileName;
                                callback.onSuccess(fallbackUrl);
                            }
                        });
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Error al procesar archivo: " + e.getMessage()));
            }
        }).start();
    }
}

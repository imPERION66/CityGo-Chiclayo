package com.greencix.citygo.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.greencix.citygo.data.model.Perfil;

public class SessionManager {

    private static final String PREF_NAME = "citygo_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDO = "apellido";
    private static final String KEY_ROL = "rol";
    private static final String KEY_ESTADO_CUENTA = "estado_cuenta";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager no está inicializado. Llama a SessionManager.init(context) primero.");
        }
        return instance;
    }

    public void saveSession(String accessToken, String refreshToken, String userId, String email) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public void savePerfil(Perfil perfil) {
        if (perfil == null) return;
        prefs.edit()
                .putString(KEY_NOMBRE, perfil.getNombre())
                .putString(KEY_APELLIDO, perfil.getApellido())
                .putString(KEY_ROL, perfil.getRol())
                .putString(KEY_ESTADO_CUENTA, perfil.getEstadoCuenta())
                .apply();
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null && !getAccessToken().isEmpty();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, "");
    }

    public String getApellido() {
        return prefs.getString(KEY_APELLIDO, "");
    }

    public String getNombreCompleto() {
        return (getNombre() + " " + getApellido()).trim();
    }

    public String getRol() {
        return prefs.getString(KEY_ROL, "ciudadano");
    }

    public String getEstadoCuenta() {
        return prefs.getString(KEY_ESTADO_CUENTA, "activo");
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}

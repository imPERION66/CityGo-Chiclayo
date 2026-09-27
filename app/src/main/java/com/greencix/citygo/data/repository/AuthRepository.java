package com.greencix.citygo.data.repository;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.model.AuthResponse;
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.network.AuthErrorMapper;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;

public class AuthRepository {

    private static AuthRepository instance;
    private final SupabaseApiClient apiClient;
    private final Gson gson;

    private AuthRepository() {
        this.apiClient = SupabaseApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    /**
     * Valida si un código de activación es válido para el rol seleccionado
     */
    public void validarCodigoActivacion(String codigo, String rol, Callback<Boolean> callback) {
        String rolSanitizado = (rol != null && !rol.trim().isEmpty()) ? rol.trim().toLowerCase() : "colaborador";
        JsonObject params = new JsonObject();
        params.addProperty("p_codigo", codigo != null ? codigo.trim().toUpperCase() : "");
        params.addProperty("p_rol", rolSanitizado);

        apiClient.rpc("validar_codigo_activacion", params.toString(), false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    boolean isValid = Boolean.parseBoolean(result.trim());
                    callback.onSuccess(isValid);
                } catch (Exception e) {
                    callback.onError(AuthErrorMapper.mapAuthError(e));
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(AuthErrorMapper.mapAuthError(errorMessage));
            }
        });
    }

    /**
     * Registra un usuario en Supabase Auth con metadatos (nombre, apellido, rol, codigo_activacion)
     * El trigger Postgres handle_new_user() crea automáticamente el perfil en la tabla 'perfiles'.
     */
    public void register(String nombre, String apellido, String email, String password, String rol, String codigoActivacion, Callback<Perfil> callback) {
        final String rolSanitizado = (rol != null && !rol.trim().isEmpty()) ? rol.trim().toLowerCase() : "ciudadano";
        final String nombreFinal = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "Usuario";
        final String apellidoFinal = (apellido != null && !apellido.trim().isEmpty()) ? apellido.trim() : "Nuevo";
        final String emailFinal = (email != null) ? email.trim() : "";
        final String codigoFinal = (codigoActivacion != null) ? codigoActivacion.trim().toUpperCase() : "";

        JsonObject dataMeta = new JsonObject();
        dataMeta.addProperty("nombre", nombreFinal);
        dataMeta.addProperty("apellido", apellidoFinal);
        dataMeta.addProperty("rol", rolSanitizado);
        dataMeta.addProperty("codigo_activacion", codigoFinal);

        JsonObject body = new JsonObject();
        body.addProperty("email", emailFinal);
        body.addProperty("password", password);
        body.add("data", dataMeta);

        Log.d("AUTH_DEBUG", "Enviando signUp a Supabase -> email: " + emailFinal + ", rol: " + rolSanitizado + ", metadata: " + dataMeta);

        apiClient.post(SupabaseConfig.AUTH_SIGNUP, body.toString(), false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("AUTH_DEBUG", "Respuesta exitosa de Supabase Auth SignUp: " + result);
                try {
                    String userId = null;
                    String accessToken = null;
                    String refreshToken = null;

                    JsonObject jsonResult = null;
                    try {
                        JsonElement parsed = JsonParser.parseString(result);
                        if (parsed.isJsonObject()) {
                            jsonResult = parsed.getAsJsonObject();
                        }
                    } catch (Exception ignored) {}

                    if (jsonResult != null) {
                        // Caso A: Objeto Session con 'user' anidado
                        if (jsonResult.has("user") && jsonResult.get("user").isJsonObject()) {
                            JsonObject userObj = jsonResult.getAsJsonObject("user");
                            if (userObj.has("id") && !userObj.get("id").isJsonNull()) {
                                userId = userObj.get("id").getAsString();
                            }
                        }
                        // Caso B: Objeto User directo en la raíz de la respuesta
                        else if (jsonResult.has("id") && !jsonResult.get("id").isJsonNull()) {
                            userId = jsonResult.get("id").getAsString();
                        }

                        if (jsonResult.has("access_token") && !jsonResult.get("access_token").isJsonNull()) {
                            accessToken = jsonResult.get("access_token").getAsString();
                        }
                        if (jsonResult.has("refresh_token") && !jsonResult.get("refresh_token").isJsonNull()) {
                            refreshToken = jsonResult.get("refresh_token").getAsString();
                        }
                    }

                    // Fallback con Gson AuthResponse
                    if (userId == null || userId.isEmpty()) {
                        AuthResponse authResponse = gson.fromJson(result, AuthResponse.class);
                        if (authResponse != null && authResponse.getUser() != null) {
                            userId = authResponse.getUser().getId();
                            if (authResponse.getAccessToken() != null) {
                                accessToken = authResponse.getAccessToken();
                                refreshToken = authResponse.getRefreshToken();
                            }
                        }
                    }

                    if (userId != null && !userId.isEmpty()) {
                        if (accessToken != null && !accessToken.isEmpty()) {
                            SessionManager.getInstance().saveSession(
                                    accessToken,
                                    refreshToken,
                                    userId,
                                    emailFinal
                            );
                        }

                        final String finalUserId = userId;
                        // Obtenemos el perfil creado por el trigger Postgres
                        fetchPerfil(finalUserId, new Callback<Perfil>() {
                            @Override
                            public void onSuccess(Perfil perfil) {
                                SessionManager.getInstance().savePerfil(perfil);
                                callback.onSuccess(perfil);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.w("AUTH_DEBUG", "fetchPerfil tras registro devolvió: " + errorMessage + ". Construyendo perfil local de respaldo.");
                                // Fallback construyendo perfil local
                                Perfil fallback = new Perfil(finalUserId, nombreFinal, apellidoFinal, emailFinal, rolSanitizado, "activo");
                                SessionManager.getInstance().savePerfil(fallback);
                                callback.onSuccess(fallback);
                            }
                        });
                    } else {
                        Log.e("AUTH_DEBUG", "Respuesta de registro sin ID de usuario válido: " + result);
                        callback.onError(AuthErrorMapper.mapAuthError(result));
                    }
                } catch (Exception e) {
                    Log.e("AUTH_DEBUG", "Error al registrarse: ", e);
                    callback.onError(AuthErrorMapper.mapAuthError(e));
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("AUTH_DEBUG", "Error exacto al registrarse en Supabase: " + errorMessage);
                callback.onError(AuthErrorMapper.mapAuthError(errorMessage));
            }
        });
    }

    /**
     * Inicia sesión con Email y Password, y luego obtiene el perfil y estado de cuenta
     */
    public void login(String email, String password, Callback<Perfil> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        apiClient.post(SupabaseConfig.AUTH_TOKEN_PASSWORD, body.toString(), false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    AuthResponse authResponse = gson.fromJson(result, AuthResponse.class);
                    if (authResponse != null && authResponse.getAccessToken() != null && authResponse.getUser() != null) {
                        String userId = authResponse.getUser().getId();
                        SessionManager.getInstance().saveSession(
                                authResponse.getAccessToken(),
                                authResponse.getRefreshToken(),
                                userId,
                                email
                        );

                        // Consultar perfil para validar rol y estado_cuenta (sanciones)
                        fetchPerfil(userId, new Callback<Perfil>() {
                            @Override
                            public void onSuccess(Perfil perfil) {
                                SessionManager.getInstance().savePerfil(perfil);
                                callback.onSuccess(perfil);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                callback.onError(errorMessage);
                            }
                        });
                    } else {
                        callback.onError(AuthErrorMapper.MSG_INVALID_CREDENTIALS);
                    }
                } catch (Exception e) {
                    callback.onError(AuthErrorMapper.mapAuthError(e));
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(AuthErrorMapper.mapAuthError(errorMessage));
            }
        });
    }

    /**
     * Obtiene el perfil del usuario desde la tabla pública perfiles
     */
    public void fetchPerfil(String userId, Callback<Perfil> callback) {
        String url = SupabaseConfig.REST_BASE + "perfiles?id=eq." + userId + "&select=*";
        apiClient.get(url, true, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JsonArray array = gson.fromJson(result, JsonArray.class);
                    if (array != null && array.size() > 0) {
                        Perfil perfil = gson.fromJson(array.get(0), Perfil.class);
                        callback.onSuccess(perfil);
                    } else {
                        callback.onError("Perfil de usuario no encontrado en la base de datos.");
                    }
                } catch (Exception e) {
                    callback.onError(AuthErrorMapper.mapAuthError(e));
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(AuthErrorMapper.mapAuthError(errorMessage));
            }
        });
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
    }
}

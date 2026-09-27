package com.greencix.citygo.data.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Mapper utilitario para convertir errores técnicos y excepciones de Supabase Auth
 * (códigos HTTP, JSON de error de GoTrue, excepciones de red) en mensajes claros y amigables.
 */
public final class AuthErrorMapper {

    public static final String MSG_USER_ALREADY_EXISTS = "Este correo electrónico ya está registrado. Por favor, inicia sesión.";
    public static final String MSG_INVALID_CREDENTIALS = "Correo o contraseña incorrectos. Verifica tus datos.";
    public static final String MSG_WEAK_PASSWORD = "La contraseña es muy débil. Debe tener al menos 6 caracteres.";
    public static final String MSG_NETWORK_ERROR = "No se pudo conectar con el servidor. Revisa tu conexión a internet.";
    public static final String MSG_INVALID_ACTIVATION_CODE = "El código de activación no es válido o ya fue utilizado.";
    public static final String MSG_GENERIC_ERROR = "Ocurrió un problema al procesar tu solicitud. Inténtalo nuevamente.";

    private AuthErrorMapper() {
        // Clase de utilidad
    }

    /**
     * Mapea un mensaje de error crudo o string de excepción a un mensaje de usuario amigable.
     */
    public static String mapAuthError(String rawError) {
        if (rawError == null || rawError.trim().isEmpty()) {
            return MSG_GENERIC_ERROR;
        }

        String rawLower = rawError.toLowerCase();

        // 1. Verificación de errores de red o timeout
        if (isNetworkError(rawLower)) {
            return MSG_NETWORK_ERROR;
        }

        // 2. Extraer y analizar posible contenido JSON de Supabase Auth
        JsonObject jsonObject = extractJsonObject(rawError);

        int code = -1;
        String errorCode = "";
        String error = "";
        String msg = "";
        String errorDescription = "";

        if (jsonObject != null) {
            if (jsonObject.has("code") && !jsonObject.get("code").isJsonNull()) {
                try {
                    code = jsonObject.get("code").getAsInt();
                } catch (Exception ignored) {}
            }
            if (jsonObject.has("error_code") && !jsonObject.get("error_code").isJsonNull()) {
                errorCode = jsonObject.get("error_code").getAsString();
            }
            if (jsonObject.has("error") && !jsonObject.get("error").isJsonNull()) {
                error = jsonObject.get("error").getAsString();
            }
            if (jsonObject.has("msg") && !jsonObject.get("msg").isJsonNull()) {
                msg = jsonObject.get("msg").getAsString();
            } else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                msg = jsonObject.get("message").getAsString();
            }
            if (jsonObject.has("error_description") && !jsonObject.get("error_description").isJsonNull()) {
                errorDescription = jsonObject.get("error_description").getAsString();
            }
        }

        // Combinar todos los detalles en minúsculas para comparaciones semánticas
        String combinedDetails = (errorCode + " " + error + " " + msg + " " + errorDescription + " " + rawLower).toLowerCase();

        // 3. Verificación de error de código de activación
        if (combinedDetails.contains("código de activación")
                || combinedDetails.contains("codigo de activacion")
                || combinedDetails.contains("codigo_activacion")
                || combinedDetails.contains("activation code")) {
            return MSG_INVALID_ACTIVATION_CODE;
        }

        // 4. Regla 1: Usuario ya registrado / error 422
        if ("user_already_exists".equalsIgnoreCase(errorCode)
                || combinedDetails.contains("user_already_exists")
                || combinedDetails.contains("already registered")
                || combinedDetails.contains("already exists")
                || combinedDetails.contains("user already exists")
                || (code == 422 && !combinedDetails.contains("weak_password") && !combinedDetails.contains("password"))
                || (rawLower.contains("(422)") && !combinedDetails.contains("weak_password") && !combinedDetails.contains("password"))) {
            return MSG_USER_ALREADY_EXISTS;
        }

        // 5. Regla 3: Contraseña débil / weak_password
        if ("weak_password".equalsIgnoreCase(errorCode)
                || combinedDetails.contains("weak_password")
                || combinedDetails.contains("password should be at least")
                || combinedDetails.contains("password must be at least")
                || combinedDetails.contains("weak password")) {
            return MSG_WEAK_PASSWORD;
        }

        // 6. Regla 2: Credenciales inválidas / error 400
        if ("invalid_grant".equalsIgnoreCase(error)
                || "invalid_grant".equalsIgnoreCase(errorCode)
                || "invalid_credentials".equalsIgnoreCase(errorCode)
                || combinedDetails.contains("invalid login credentials")
                || combinedDetails.contains("invalid_grant")
                || combinedDetails.contains("invalid_credentials")
                || combinedDetails.contains("credenciales incorrectas")
                || (code == 400 && (combinedDetails.contains("invalid") || combinedDetails.contains("credentials") || combinedDetails.contains("grant")))) {
            return MSG_INVALID_CREDENTIALS;
        }

        // 7. Regla 5: Error inesperado / genérico limpio
        return MSG_GENERIC_ERROR;
    }

    /**
     * Mapea un objeto Throwable (Excepción) a un mensaje amigable.
     */
    public static String mapAuthError(Throwable throwable) {
        if (throwable == null) {
            return MSG_GENERIC_ERROR;
        }

        if (throwable instanceof SocketTimeoutException
                || throwable instanceof UnknownHostException
                || throwable instanceof ConnectException
                || (throwable instanceof IOException && isNetworkError(throwable.getMessage()))) {
            return MSG_NETWORK_ERROR;
        }

        return mapAuthError(throwable.getMessage());
    }

    /**
     * Mapea directamente el código HTTP y el cuerpo de respuesta.
     */
    public static String mapAuthError(int httpCode, String responseBody) {
        if (httpCode == 422) {
            String mapped = mapAuthError(responseBody);
            if (MSG_GENERIC_ERROR.equals(mapped)) {
                return MSG_USER_ALREADY_EXISTS;
            }
            return mapped;
        }
        if (httpCode == 400) {
            String mapped = mapAuthError(responseBody);
            if (MSG_GENERIC_ERROR.equals(mapped)) {
                return MSG_INVALID_CREDENTIALS;
            }
            return mapped;
        }
        return mapAuthError(responseBody);
    }

    private static boolean isNetworkError(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();
        return lower.contains("error de conexión")
                || lower.contains("timeout")
                || lower.contains("timed out")
                || lower.contains("unable to resolve host")
                || lower.contains("failed to connect")
                || lower.contains("no address associated with hostname")
                || lower.contains("connection refused")
                || lower.contains("network is unreachable")
                || lower.contains("software caused connection abort")
                || lower.contains("socket");
    }

    private static JsonObject extractJsonObject(String text) {
        if (text == null) return null;
        int startIndex = text.indexOf('{');
        int endIndex = text.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            String jsonPart = text.substring(startIndex, endIndex + 1);
            try {
                JsonElement element = JsonParser.parseString(jsonPart);
                if (element.isJsonObject()) {
                    return element.getAsJsonObject();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}

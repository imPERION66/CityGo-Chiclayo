package com.greencix.citygo.data.network;

import org.junit.Test;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class AuthErrorMapperTest {

    @Test
    public void testUserAlreadyExists_JsonWithErrorCode() {
        String raw = "{\"code\":422,\"error_code\":\"user_already_exists\",\"msg\":\"User already registered\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_USER_ALREADY_EXISTS, result);
    }

    @Test
    public void testUserAlreadyExists_RawApiClientErrorWithHttp422() {
        String raw = "Error en la solicitud (422): {\"code\":422,\"msg\":\"User already registered\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_USER_ALREADY_EXISTS, result);
    }

    @Test
    public void testUserAlreadyExists_WithAlreadyExistsText() {
        String raw = "Error en la solicitud (422): {\"message\":\"A user with this email already exists\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_USER_ALREADY_EXISTS, result);
    }

    @Test
    public void testUserAlreadyExists_HttpCodeMapper() {
        String result = AuthErrorMapper.mapAuthError(422, "{\"error\":\"Unprocessable Entity\"}");
        assertEquals(AuthErrorMapper.MSG_USER_ALREADY_EXISTS, result);
    }

    @Test
    public void testInvalidCredentials_JsonWithInvalidGrant() {
        String raw = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid login credentials\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_INVALID_CREDENTIALS, result);
    }

    @Test
    public void testInvalidCredentials_RawApiClientErrorWithHttp400() {
        String raw = "Error en la solicitud (400): {\"error\":\"invalid_grant\",\"error_description\":\"Invalid login credentials\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_INVALID_CREDENTIALS, result);
    }

    @Test
    public void testInvalidCredentials_ErrorCode() {
        String raw = "{\"code\":400,\"error_code\":\"invalid_credentials\",\"msg\":\"Invalid login credentials\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_INVALID_CREDENTIALS, result);
    }

    @Test
    public void testWeakPassword_JsonWithWeakPassword() {
        String raw = "{\"code\":422,\"error_code\":\"weak_password\",\"msg\":\"Password should be at least 6 characters\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_WEAK_PASSWORD, result);
    }

    @Test
    public void testWeakPassword_RawApiClientError() {
        String raw = "Error en la solicitud (422): {\"msg\":\"Password should be at least 6 characters\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_WEAK_PASSWORD, result);
    }

    @Test
    public void testNetworkError_RawStringConnection() {
        String raw = "Error de conexión: failed to connect to xyz.supabase.co/127.0.0.1:443";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, result);
    }

    @Test
    public void testNetworkError_Timeout() {
        String raw = "Error de conexión: timeout";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, result);
    }

    @Test
    public void testNetworkError_UnableToResolveHost() {
        String raw = "Error de conexión: Unable to resolve host \"supabase.co\": No address associated with hostname";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, result);
    }

    @Test
    public void testNetworkError_ThrowableInstances() {
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, AuthErrorMapper.mapAuthError(new SocketTimeoutException("connect timed out")));
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, AuthErrorMapper.mapAuthError(new UnknownHostException("xyz.supabase.co")));
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, AuthErrorMapper.mapAuthError(new ConnectException("Connection refused")));
        assertEquals(AuthErrorMapper.MSG_NETWORK_ERROR, AuthErrorMapper.mapAuthError(new IOException("Failed to connect to /1.2.3.4:443")));
    }

    @Test
    public void testGenericError_NullAndEmpty() {
        assertEquals(AuthErrorMapper.MSG_GENERIC_ERROR, AuthErrorMapper.mapAuthError((String) null));
        assertEquals(AuthErrorMapper.MSG_GENERIC_ERROR, AuthErrorMapper.mapAuthError("   "));
        assertEquals(AuthErrorMapper.MSG_GENERIC_ERROR, AuthErrorMapper.mapAuthError((Throwable) null));
    }

    @Test
    public void testGenericError_UnexpectedServerError() {
        String raw = "Error en la solicitud (500): {\"message\":\"Database internal unexpected failure\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_GENERIC_ERROR, result);
    }

    @Test
    public void testInvalidActivationCode_ErrorMessage() {
        String raw = "Error en la solicitud (500): {\"message\":\"El código de activación no es válido para el rol solicitado.\"}";
        String result = AuthErrorMapper.mapAuthError(raw);
        assertEquals(AuthErrorMapper.MSG_INVALID_ACTIVATION_CODE, result);
    }

    @Test
    public void testGenericError_UnknownException() {
        Exception ex = new RuntimeException("Unexpected parsing error occurred in internal flow");
        String result = AuthErrorMapper.mapAuthError(ex);
        assertEquals(AuthErrorMapper.MSG_GENERIC_ERROR, result);
    }
}

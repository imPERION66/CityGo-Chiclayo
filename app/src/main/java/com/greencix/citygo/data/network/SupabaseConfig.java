package com.greencix.citygo.data.network;

public class SupabaseConfig {
    public static final String SUPABASE_URL = "https://cbpbskkarlkypaerwgpc.supabase.co";
    public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNicGJza2thcmxreXBhZXJ3Z3BjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTAwNTAwMTAsImV4cCI6MjEwNTYyNjAxMH0.0wg2t8c2SN2pOk0F2zFhwd_vADJ0eYVu4kpDpgf52rE";

    // Endpoints
    public static final String AUTH_SIGNUP = SUPABASE_URL + "/auth/v1/signup";
    public static final String AUTH_TOKEN_PASSWORD = SUPABASE_URL + "/auth/v1/token?grant_type=password";
    public static final String AUTH_LOGOUT = SUPABASE_URL + "/auth/v1/logout";
    public static final String AUTH_USER = SUPABASE_URL + "/auth/v1/user";

    public static final String REST_BASE = SUPABASE_URL + "/rest/v1/";
    public static final String RPC_BASE = SUPABASE_URL + "/rest/v1/rpc/";
}

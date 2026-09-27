package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("user_metadata")
    private Map<String, Object> userMetadata;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Map<String, Object> getUserMetadata() { return userMetadata; }
    public void setUserMetadata(Map<String, Object> userMetadata) { this.userMetadata = userMetadata; }
}

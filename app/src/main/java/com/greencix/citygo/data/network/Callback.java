package com.greencix.citygo.data.network;

public interface Callback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}

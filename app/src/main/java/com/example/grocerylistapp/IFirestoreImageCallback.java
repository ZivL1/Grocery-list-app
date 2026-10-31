package com.example.grocerylistapp;

public interface IFirestoreImageCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}

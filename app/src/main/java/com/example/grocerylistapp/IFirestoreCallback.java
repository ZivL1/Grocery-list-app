package com.example.grocerylistapp;

import java.util.ArrayList;

public interface IFirestoreCallback {
    void onDataReceived(ArrayList<Grocery> groceries);
}

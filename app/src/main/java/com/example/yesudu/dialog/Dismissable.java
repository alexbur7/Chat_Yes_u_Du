package com.example.yesudu.dialog;

public interface Dismissable<T> {
    void onDismiss(T code);
    String chooseOption(T code);
}

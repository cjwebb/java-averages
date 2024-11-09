package com.cjwebb.javaaverages.client;

@FunctionalInterface
public interface MessageHandler {
    void onMessage(String message);
}

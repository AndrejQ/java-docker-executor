package ru.my.java.executor.configuration;

import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface CommandExecutor {
    String execute(String command, int timeoutSec) throws TimeoutException;
}

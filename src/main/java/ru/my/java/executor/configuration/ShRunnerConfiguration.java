package ru.my.java.executor.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class ShRunnerConfiguration {

    @Bean
    public CommandExecutor commandExecutor() {
        return this::executeCommand;
    }

    @SneakyThrows(value = {IOException.class, InterruptedException.class, ExecutionException.class})
    private String executeCommand(String command, int timeoutSec) throws TimeoutException {
        var resultSb = new StringBuilder();
        Process process = Runtime.getRuntime().exec(command);
        StreamGobbler streamGobbler
                = new StreamGobbler(process.getInputStream(), process.getErrorStream(), resultSb::append);
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
        boolean success = process.waitFor(timeoutSec, TimeUnit.SECONDS);
        if (success) {
            future.get(); // waits for streamGobbler to finish
            return resultSb.toString();
        }
        else {
            future.cancel(true);
            throw new TimeoutException("timeout sec: " + timeoutSec);
        }
    }

    private record StreamGobbler(InputStream inputStream,
                                 InputStream errorStream,
                                 Consumer<String> consumer) implements Runnable {
        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(errorStream)).lines()
                    .forEach(consumer);
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}

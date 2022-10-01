package ru.my.java.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.my.java.executor.configuration.CommandExecutor;

import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnApplicationReadyListener {
    private final CommandExecutor commandExecutor;

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) throws TimeoutException {
        log.info("start pulling openjdk:17-jdk-slim");
        log.info(commandExecutor.execute("docker pull openjdk:17-jdk-slim", 30));
    }
}

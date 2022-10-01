package ru.my.java.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import ru.my.java.executor.worker.WorkerPool;

@Slf4j
@Component
@RequiredArgsConstructor
public class GracefulShutdownListener implements ApplicationListener<ContextClosedEvent> {
    private final WorkerPool workerPool;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (event.getSource() instanceof AnnotationConfigServletWebServerApplicationContext) {
            workerPool.clear();
        }
        log.info("SHUTDOWN");
    }
}
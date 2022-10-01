package ru.my.java.executor.worker;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerPool {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final AtomicInteger workerOrdinal = new AtomicInteger(0);
    private final Map<String, Long> workers = new ConcurrentHashMap<>();

    private final WorkerExecutor workerExecutor;

    @SneakyThrows(value = {InterruptedException.class, ExecutionException.class})
    public String runJavaCode(String code) {
        String nextWorkerName = "java_worker_" + workerOrdinal.incrementAndGet();
        Future<String> result = threadPool.submit(() -> {
            workers.put(nextWorkerName, System.currentTimeMillis());
            return workerExecutor.runJavaCode(code, nextWorkerName);
        });
        try {
            return result.get();
        } finally {
            workers.remove(nextWorkerName);
        }
    }

    public List<String> getAll() {
        return workers.keySet()
                .stream()
                .sorted()
                .toList();
    }

    public void clear() {
        workers.keySet().forEach(workerExecutor::rmWorker);
    }
}

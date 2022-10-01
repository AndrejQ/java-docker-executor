package ru.my.java.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.my.java.executor.worker.WorkerPool;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoordinatorService {
    private final WorkerPool workerPool;

    public String runJavaCode(String code) {
        return workerPool.runJavaCode(code);
    }

    public List<String> getAllWorkers() {
        return workerPool.getAll();
    }
}

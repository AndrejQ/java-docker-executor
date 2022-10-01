package ru.my.java.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CoordinatorController {
    private final CoordinatorService coordinatorService;
    @PostMapping(path = "/")
    public String runJavaCode(@RequestBody String code) {
        return coordinatorService.runJavaCode(code);
    }

    @GetMapping(path = "/")
    public List<String> getAllWorkers() {
        return coordinatorService.getAllWorkers();
    }
}

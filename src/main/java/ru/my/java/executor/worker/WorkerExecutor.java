package ru.my.java.executor.worker;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.my.java.executor.configuration.CommandExecutor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Component
public class WorkerExecutor {
    private static final String FILE_NAME = "Main_%s.java";
    private static final String PLACEHOLDER_MAIN_JAVA_FILE = """
            import java.util.*;
            import java.io.*;
            import java.lang.*;
            import java.math.*;
            import java.net.*;
            import java.nio.*;
            import java.security.*;
            import java.text.*;
            import java.time.*;
                            
            public class Main_%s {
                public static void main(String[] args) {
                    %s
                }
                
                private static void print(Object s) {
                    System.out.println(s);
                }
                
                private static void sleep(long millis) {
                    try {
                        Thread.sleep(millis);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
            """;

    private final CommandExecutor commandExecutor;

    @Value("${java-classes-dir}")
    private String javaClassesDir;

    @Value("${exec-timeout-sec: 10}")
    private int execTimeoutSec;

    @Value("${sh-script-dir}")
    private String scriptPath;

    @SneakyThrows(value = {IOException.class})
    public String runJavaCode(String code, String workerName) {
        createJavaFile(code, workerName);
        try {
            return commandExecutor.execute(String.format("sh %s/workerRun.sh %s %s", scriptPath, workerName, javaClassesDir), execTimeoutSec);
        } catch (TimeoutException e) {
            return "Timeout sec: " + execTimeoutSec + ", worker: " + workerName;
        } finally {
            rmWorker(workerName);
        }
    }


    public void rmWorker(String workerName) {
        rmContainer(workerName);
        rmFile(workerName);
    }

    private void createJavaFile(String code, String workerName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(buildFileName(workerName)));
        String javaCodeFile = javaCodeFile(code, workerName);
        writer.write(javaCodeFile);
        writer.close();
    }

    private void rmContainer(String workerName) {
        try {
            commandExecutor.execute("docker rm -f " + workerName, 10);
        } catch (TimeoutException e) {
            log.error("rm failed for: " + workerName, e);
        }
    }

    private String javaCodeFile(String code, String workerName) {
        return String.format(PLACEHOLDER_MAIN_JAVA_FILE, workerName, code);
    }

    private void rmFile(String workerName) {
        String fileName = buildFileName(workerName);
        boolean deleted = new File(fileName).delete();
        if (!deleted) {
            log.error("file {} not deleted", fileName);
        }
    }

    private String buildFileName(String workerName) {
        return String.format(javaClassesDir + "/" + FILE_NAME, workerName);
    }
}

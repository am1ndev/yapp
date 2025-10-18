package dev.amin.api.client;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Component
public class OllamaClient {

    @Value("${spring.ai.ollama.chat.model}")
    private String model;

    private Process process;

    //    @PostConstruct
    public void run() {
        try {
            if (running()) {
                log.info("Ollama server already running.");
                return;
            }

            ProcessBuilder builder = new ProcessBuilder("ollama", "serve", model);
            builder.redirectErrorStream(true);
            process = builder.start();

            log.info("Ollama server started.");

            // log model output for debugging
//            new Thread(() -> {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        log.debug("[ollama] {}", line);
//                    }
//                } catch (IOException ignored) {}
//            }).start();
        } catch (IOException e) {
            log.error("Failed to start Ollama server: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void stop() {
        try {
            new ProcessBuilder("ollama", "stop", model).start();
            log.info("Ollama server stopped.");

            if (process != null && process.isAlive()) {
                process.destroy();
            }
        } catch (IOException e) {
            log.error("Error stopping Ollama: {}", e.getMessage());
        }
    }

    private boolean running() {
        try {
            Process process = new ProcessBuilder("ollama", "ps").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().anyMatch(line -> line.contains(model));
            }
        } catch (IOException e) {
            return false;
        }
    }
}

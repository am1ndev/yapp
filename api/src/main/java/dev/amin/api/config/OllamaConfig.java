package dev.amin.api.config;

import dev.amin.api.client.OllamaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class OllamaConfig {

    private final OllamaClient ollamaClient;

    @Value("classpath:/prompts/system.st")
    private Resource prompt;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
//                .defaultSystem(prompt)
                .build();
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> ollamaClient.run();
    }
}
 
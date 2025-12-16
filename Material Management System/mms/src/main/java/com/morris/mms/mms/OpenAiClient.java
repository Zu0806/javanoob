package com.morris.mms.mms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiClient {

    private final RestClient client;
    private final boolean enabled;

    public OpenAiClient(
            @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl
    ) {
        String apiKey = System.getenv("OPENAI_API_KEY");

        // 🔴 沒有 key：進入 fallback 模式（不丟例外！）
        if (apiKey == null || apiKey.isBlank()) {
            this.enabled = false;
            this.client = null;
            System.out.println("[AI] OPENAI_API_KEY not set → fallback mode");
            return;
        }

        // ✅ 有 key 才初始化 client
        this.enabled = true;
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 只有 enabled=true 才會被呼叫
     */
    @SuppressWarnings("unchecked")
    public String simpleJson(String model, String prompt) {
        if (!enabled) {
            throw new IllegalStateException("OpenAI is disabled (fallback mode)");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "input", prompt
        );

        Map<String, Object> resp = client.post()
                .uri("/responses")
                .body(body)
                .retrieve()
                .body(Map.class);

        Object out = resp.get("output_text");
        return out == null ? "" : out.toString();
    }
}

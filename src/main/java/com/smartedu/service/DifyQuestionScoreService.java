package com.smartedu.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class DifyQuestionScoreService {

    private static final String API_URL = "http://localhost/v1/workflows/run";
    private static final String API_KEY = "app-RavOI6Z2HV4A7AYH9MgPmEJi";

    public String runWorkflow(String question, String solution, String answer, String score) {
        String payload = String.format(
                """
                {
                  "inputs": {
                    "question": "%s",
                    "solution": "%s",
                    "answer": "%s",
                    "score": "%s"
                  },
                  "response_mode": "blocking",
                  "user": "abc-123"
                }
                """,
                question, solution, answer, score
        );

        try {
            // 1. 建立连接
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);

            // 2. 发送请求体
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            // 3. 读取响应
            int status = conn.getResponseCode();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            // 4. 使用 Jackson 解析并自动 unescape
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(sb.toString());
            JsonNode contentNode = root.path("data")
                    .path("outputs")
                    .path("text");
            return contentNode.asText();

        } catch (Exception e) {
            throw new RuntimeException("请求失败: " + e.getMessage(), e);
        }
    }
}

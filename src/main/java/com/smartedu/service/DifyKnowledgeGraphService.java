package com.smartedu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class DifyKnowledgeGraphService {
    private static final String API_URL = "http://localhost/v1/workflows/run";
    private static final String API_KEY = "app-YLshb5AxkcPr1DhtCGcMb8x9";

    public JsonNode runWorkflow(String knowledge) {
        String payload = String.format(
                """
                {
                  "inputs": {
                    "knowledge": "%s"
                  },
                  "response_mode": "blocking",
                  "user": "abc-123"
                }
                """,
                knowledge
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
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 3. 读取响应
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // 4. 解析响应并直接返回JSON数据
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());
            String text = root.path("data")
                    .path("outputs")
                    .path("text")
                    .asText();

            // 5. 解析返回的文本中的JSON部分
            int jsonStart = text.indexOf('{');
            int jsonEnd = text.lastIndexOf('}');

            if (jsonStart == -1 || jsonEnd == -1) {
                throw new RuntimeException("未找到有效的知识图谱数据");
            }

            String jsonStr = text.substring(jsonStart, jsonEnd + 1)
                    .replaceAll("\\\\n", "\n")
                    .replaceAll("\\\\\"", "\"")
                    .replaceAll("\\\\\\\\", "\\\\");

            // 打印处理后的JSON字符串
            System.out.println("======= 发送给前端的知识图谱JSON数据 =======");
            System.out.println(mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mapper.readTree(jsonStr)));
            System.out.println("=======================================");

            return mapper.readTree(jsonStr);

        } catch (Exception e) {
            throw new RuntimeException("处理知识图谱失败: " + e.getMessage(), e);
        }
    }
}

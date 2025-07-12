package com.smartedu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.entity.Question;
import com.smartedu.entity.StudentAnswer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DifyRecommendService {
    private static final String API_URL = "http://localhost/v1/workflows/run";
    private static final String API_KEY = "app-uWeKTZqm3qQLsWGyq8xkn4Fb";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private StudentAnswerService studentAnswerService;

    @Resource
    private QuestionService questionService;

    // 获取学生知识点掌握情况
    private Map<String, Double> calculateKnowledgePointScores(Long studentId) {
        // 用于存储每个知识点的所有得分率
        Map<String, List<Double>> pointScores = new HashMap<>();

        // 获取学生的所有答题记录
        StudentAnswer query = new StudentAnswer();
        query.setStudentId(studentId);
        List<StudentAnswer> answers = studentAnswerService.selectAll(query);

        // 计算每个知识点的得分率
        for (StudentAnswer answer : answers) {
            Question question = questionService.selectById(answer.getQuestionId());
            if (question != null && question.getKgPoint() != null) {
                // 计算单题得分率
                double scoreRate = answer.getGetscore() / question.getScore();

                // 添加到对应知识点的得分列表中
                pointScores.computeIfAbsent(question.getKgPoint(), k -> new ArrayList<>())
                          .add(scoreRate);
            }
        }

        // 计算每个知识点的平均得分率
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : pointScores.entrySet()) {
            double averageScore = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(-1.0); // 如果没有做过该知识点的题目，返回-1
            result.put(entry.getKey(), averageScore);
        }

        return result;
    }

    public String getRecommendation(Long studentId) {
        // 获取学生知识点掌握情况
        Map<String, Double> scores = calculateKnowledgePointScores(studentId);

        // 构建输入文本
        StringBuilder studyInfo = new StringBuilder("学生各知识点掌握情况如下：\n");
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            String status = entry.getValue() == -1 ?
                "未学习" :
                String.format("掌握程度为%.1f%%", entry.getValue() * 100);
            studyInfo.append(entry.getKey()).append(": ").append(status).append("\n");
        }

        // 调用智能体获取建议
        String rawResponse = runWorkflow(studyInfo.toString());
        System.out.println("原始返回内容: " + rawResponse);

        // 解析响应获取实际的推荐文本
        try {
            return extractRecommendationText(rawResponse);
        } catch (Exception e) {
            System.err.println("解析推荐内容失败: " + e.getMessage());
            e.printStackTrace();
            return "解析推荐内容失败，请联系管理员检查系统日志";
        }
    }

    // 从API响应中提取文本内容 - 修改后的方法
    private String extractRecommendationText(String response) {
        try {
            // 解析最外层JSON
            JsonNode rootNode = objectMapper.readTree(response);

            // 正确的路径: data -> outputs -> text
            if (rootNode.has("data") &&
                rootNode.get("data").has("outputs") &&
                rootNode.get("data").get("outputs").has("text")) {

                String text = rootNode.get("data").get("outputs").get("text").asText();

                // 移除<think>标签中的内容，只保留最终建议
                if (text.contains("<think>") && text.contains("</think>")) {
                    text = text.replaceAll("<think>[\\s\\S]*?</think>", "").trim();
                }

                return text;
            } else {
                // 输出错误信息以便调试
                System.err.println("无法在响应中找到预期的字段结构");
                System.err.println("响应结构: " + response);
                return "无法解析推荐内容，响应结构不符合预期";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "解析推荐内容时出错: " + e.getMessage();
        }
    }

    public String runWorkflow(String study) {
        String payload = String.format(
                """
                {
                  "inputs": {
                    "study": "%s"
                  },
                  "response_mode": "blocking",
                  "user": "abc-123"
                }
                """,
                study.replace("\"", "\\\"").replace("\n", "\\n")
        );

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置请求方式和请求头
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 发送请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            // 读取响应
            int status = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            status >= 200 && status < 300 ?
                            conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "获取学习建议失败：" + e.getMessage();
        }
    }
}

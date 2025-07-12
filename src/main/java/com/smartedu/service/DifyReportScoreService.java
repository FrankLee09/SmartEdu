package com.smartedu.service;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.regex.Matcher;

@Component
public class DifyReportScoreService {

    private static final String API_BASE_URL = "http://localhost/v1";
    private static final String API_KEY = "app-IEVWsADrZOGEEh6dT8Gj5Jv6";
    private static final Pattern SCORE_PATTERN = Pattern.compile("(\\d{2,3})(?![\\d%])");

    public String uploadFile(String filePath, String userId) {
        String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.err.println("文件不存在：" + filePath);
                return null;
            }

            String fileName = path.getFileName().toString();
            String mimeType = getMimeType(fileName);
            if (mimeType == null) {
                System.err.println("不支持的文件类型：" + fileName);
                return null;
            }

            URL url = new URL(API_BASE_URL + "/files/upload");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {

                // 文件部分
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
                writer.append("Content-Type: ").append(mimeType).append("\r\n\r\n");
                writer.flush();
                Files.copy(path, os);
                os.flush();
                writer.append("\r\n").flush();

                // 用户字段
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"user\"\r\n\r\n");
                writer.append(userId).append("\r\n");

                // 结束边界
                writer.append("--").append(boundary).append("--\r\n").flush();
            }

            int status = conn.getResponseCode();
            InputStream input = (status == 201) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            reader.lines().forEach(response::append);

            System.out.println("上传响应状态码: " + status);
            System.out.println("上传响应内容: " + response);

            if (status == 201) {
                return extractFileId(response.toString());
            } else {
                System.err.println("上传失败，状态码：" + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String runWorkflow(String fileId, String userId) {
        try {
            // 构造请求 JSON 体
            JSONObject reportObj = new JSONObject();
            reportObj.put("type", "document");
            reportObj.put("transfer_method", "local_file");
            reportObj.put("upload_file_id", fileId);

            JSONObject inputsObj = new JSONObject();
            inputsObj.put("report", reportObj);

            JSONObject requestBody = new JSONObject();
            requestBody.put("inputs", inputsObj);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", userId);

            // 建立连接（注意用工作流接口地址）
            URL url = new URL(API_BASE_URL + "/workflows/run");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            InputStream inputStream = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();

            String responseStr;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
                responseStr = sb.toString();
            }

            System.out.println("工作流响应状态码: " + status);
            System.out.println("工作流响应内容: " + responseStr);

            if (status >= 200 && status < 300) {
                JSONObject jsonObject = new JSONObject(responseStr);
                // 这里根据实际返回结构解析，这里示例提取 outputs.text
                String outputText = extractOutputText(responseStr);
                if (outputText != null) {
                    // 清除 <think> 标签段落
                    outputText = outputText.replaceAll("<think>.*?</think>", "").trim();

                    Matcher matcher = SCORE_PATTERN.matcher(outputText);
                    if (matcher.find()) {
                        return matcher.group(1);
                    } else {
                        return "无法提取得分";
                    }
                } else {
                    return "无有效输出文本";
                }
            } else {
                return "工作流执行失败，状态码：" + status;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "系统异常：" + e.getMessage();
        }
    }

    private String getMimeType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "pdf" -> "application/pdf";
            case "png" -> "application/png";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> null;
        };
    }

    private String extractFileId(String responseJson) {
        int start = responseJson.indexOf("\"id\"");
        if (start != -1) {
            start = responseJson.indexOf(":", start) + 1;
            while (responseJson.charAt(start) == ' ' || responseJson.charAt(start) == '"') start++;
            int end = responseJson.indexOf("\"", start);
            if (end != -1) return responseJson.substring(start, end);
        }
        return null;
    }

    private String extractOutputText(String json) {
        int outputs = json.indexOf("\"outputs\"");
        if (outputs == -1) return null;
        int textIdx = json.indexOf("\"text\"", outputs);
        int colon = json.indexOf(":", textIdx);
        int q1 = json.indexOf("\"", colon + 1);
        int q2 = json.indexOf("\"", q1 + 1);
        return (q1 != -1 && q2 != -1) ? json.substring(q1 + 1, q2) : null;
    }
}


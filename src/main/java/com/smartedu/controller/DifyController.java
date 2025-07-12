package com.smartedu.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartedu.service.DifyReportScoreService;
import com.smartedu.common.Result;
import com.smartedu.entity.Exam;
import com.smartedu.entity.File;
import com.smartedu.entity.Question;
import com.smartedu.entity.StudentAnswer;
import com.smartedu.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/dify")
public class DifyController {

    @Resource
    private DifyQuestionScoreService difyQuestionScoreService;

    @Resource
    private StudentAnswerService studentAnswerService;

    @Resource
    private DifyKnowledgeGraphService difyKnowledgeGraphService;

    @Resource
    private DifyRecommendService difyRecommendService;

    @Resource
    private DifyReportScoreService difyReportScoreService;


    public static class QuestionScoreRequest {
        private Question theQuestion;
        private Exam exam;
        private Long studentId;
        private String solution;

        public Question getTheQuestion() {
            return theQuestion;
        }

        public void setTheQuestion(Question theQuestion) {
            this.theQuestion = theQuestion;
        }

        public Exam getExam() {
            return exam;
        }

        public void setExam(Exam exam) {
            this.exam = exam;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getSolution() {
            return solution;
        }

        public void setSolution(String solution) {
            this.solution = solution;
        }
    }

    public static class ReportScoreRequest {
        private File report;

        public File getReport() {
            return report;
        }

        public void setReport(File report) {
            this.report = report;
        }
    }

    public static class KnowledgeGraphRequest {
        private String knowledge;

        public String getKnowledge() {
            return knowledge;
        }

        public void setKnowledge(String knowledge) {
            this.knowledge = knowledge;
        }
    }


    @PostMapping("/knowledgeGraph")
    public Result knowledgeGraph(@RequestBody KnowledgeGraphRequest request) {
        String knowledge = request.getKnowledge();
        if (knowledge == null || knowledge.trim().isEmpty()) {
            return Result.error("400", "知识点内容不能为空");
        }
        try {
            System.out.println("知识图谱输入内容: " + knowledge);
            JsonNode graph = difyKnowledgeGraphService.runWorkflow(knowledge);
            return Result.success(graph);
        } catch (Exception e) {
            System.err.println("知识图谱服务异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "知识图谱服务异常: " + e.getMessage());
        }
    }

    @PostMapping("/questionScore")
    public Result questionScore(@RequestBody QuestionScoreRequest request) {
        Question theQuestion = request.getTheQuestion();
        Exam exam = request.getExam();
        Long studentId = request.getStudentId();
        String solution = request.getSolution();

        String question = theQuestion.getDescription();
        String answer = theQuestion.getAnswer();
        String score = String.valueOf(theQuestion.getScore());
        String result = difyQuestionScoreService.runWorkflow(question, solution, answer, score);
        System.out.println(result);
        Double getScore = 0.0;
        Pattern p = Pattern.compile("(\\d+)\\s*$");
        Matcher m = p.matcher(result);
        if (m.find()) {
            String scoreStr = m.group(1);
            try {
                getScore = Double.parseDouble(scoreStr);
            } catch (NumberFormatException ex) {
                return Result.error("500", "大模型错误");
            }
        } else {
            return Result.error("500", "大模型错误");
        }
        System.out.println("得分: " + getScore);

        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setStudentId(studentId);
        studentAnswer.setQuestionId(theQuestion.getId());
        studentAnswer.setExamId(exam.getId());
        studentAnswer.setGetscore(getScore);
        studentAnswer.setAnswer(solution);

        studentAnswerService.add(studentAnswer);

        return Result.success();
    }

    @PostMapping("/reportScore")
    public Result reportScore(@RequestParam("file") MultipartFile file,
                             @RequestParam("userId") String userId) {
        try {
            // 创建临时文件
            java.io.File tempFile = java.io.File.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile);

            // 上传文件获取fileId
            String fileId = difyReportScoreService.uploadFile(tempFile.getAbsolutePath(), userId);

            if (fileId == null) {
                return Result.error("500", "文件上传失败");
            }

            // 运行工作流
            String result = difyReportScoreService.runWorkflow(fileId, userId);

            // 检查结果是否有效
            if (result == null || result.isEmpty() ||
                result.contains("无法提取得分") ||
                result.contains("系统异常")) {
                return Result.error("500", result != null ? result : "文件处理失败");
            }

            // 成功情况下返回结果
            return Result.success(result);
        } catch (IOException e) {
            return Result.error("500", "文件处理异常: " + e.getMessage());
        }
    }

    @GetMapping("/recommend/{studentId}")
    public Result getRecommendation(@PathVariable Long studentId) {
        try {
            System.out.println("接收到学习推荐请求，学生ID: " + studentId);
            String recommendation = difyRecommendService.getRecommendation(studentId);
            if (recommendation != null && !recommendation.isEmpty() &&
                !recommendation.startsWith("解析推荐内容失败") &&
                !recommendation.startsWith("无法解析推荐内容")) {
                System.out.println("成功获取推荐内容");
                return Result.success(recommendation);
            } else {
                System.err.println("获取推荐内容失败: " + recommendation);
                return Result.error("500", "无法生成学习建议，请稍后再试");
            }
        } catch (Exception e) {
            System.err.println("获取学习建议失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "获取学习建议失败: " + e.getMessage());
        }
    }

}

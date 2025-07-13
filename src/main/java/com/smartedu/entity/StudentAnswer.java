package com.smartedu.entity;

public class StudentAnswer {
    Long id;
    Long studentId;
    Long questionId;
    Long examId;
    Double getscore;
    String answer;

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setGetscore(Double getscore) {
        this.getscore = getscore;
    }

    public String getAnswer() {
        return answer;
    }
    public Long getId() {
        return id;
    }
    public Long getStudentId() {
        return studentId;
    }

    public Long getExamId() {
        return examId;
    }

    public Double getGetscore() {
        return getscore;
    }

    public Long getQuestionId() {
        return questionId;
    }


}


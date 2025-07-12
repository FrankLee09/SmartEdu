package com.smartedu.entity;

public class Question {
    Long id;
    String title;
    String description;
    Long courseId;
    String selectA;
    String selectB;
    String selectC;
    String selectD;
    String answer;
    String kgPoint;
    String tag;
    Double score;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setKgPoint(String kgPoint) {
        this.kgPoint = kgPoint;
    }

    public void setSelectA(String selectA) {
        this.selectA = selectA;
    }

    public void setSelectB(String selectB) {
        this.selectB = selectB;
    }

    public void setSelectC(String selectC) {
        this.selectC = selectC;
    }

    public void setSelectD(String selectD) {
        this.selectD = selectD;
    }


    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAnswer() {
        return answer;
    }

    public String getKgPoint() {
        return kgPoint;
    }

    public String getSelectA() {
        return selectA;
    }

    public String getSelectB() {
        return selectB;
    }

    public String getSelectC() {
        return selectC;
    }

    public String getSelectD() {
        return selectD;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTag() {
        return tag;
    }

    public Double getScore() {
        return score;
    }
}

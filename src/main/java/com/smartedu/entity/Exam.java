package com.smartedu.entity;

import java.util.Date;
import java.util.List;


public class Exam {
    Long id;
    List<Long> questionIds;
    Long courseId;
    Double totalscore;

    String startTime;

    String endTime;
    String title;
    String tag;

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

    public void setTotalscore(Double totalscore) {
        this.totalscore = totalscore;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setTotalScore(Double totalscore) {
        this.totalscore = totalscore;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTag() {
        return tag;
    }
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getEndTime() {
        return endTime;
    }

    public Double getTotalscore() {
        return totalscore;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public String getStartTime() {
        return startTime;
    }
    public Double getTotalScore() {
        return totalscore;
    }


}

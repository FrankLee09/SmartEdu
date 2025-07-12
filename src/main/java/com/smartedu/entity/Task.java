package com.smartedu.entity;

import java.util.Date;
import java.util.List;

public class Task {
    Long id ;
    String title;
    String content;
    List<Long> classIds;
    String dueDate;
    String filename;
    String fileUrl;
    String tag;
    String time;
    Long courseId;
    private List<Long> completedStudentIds;

    public void setCompletedStudentIds(List<Long> completedStudentIds) {
        this.completedStudentIds = completedStudentIds;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setId(Long id) {this.id = id;}


    public void setClassIds(List<Long> classIds) {
        this.classIds = classIds;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }


    public List<Long> getClassIds() {
        return classIds;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getContent() {
        return content;
    }


    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getTag() {
        return tag;
    }

    public String getTime() {
        return time;
    }

    public Long getCourseId() {
        return courseId;
    }

    public List<Long> getCompletedStudentIds() {
        return completedStudentIds;
    }
}


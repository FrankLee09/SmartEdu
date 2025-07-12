package com.smartedu.entity;

import java.util.Date;

public class Submission {
    Long id;
    Long studentId;
    Long taskId;
    String fileUrl;
    String submitTime;
    String filename;


    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFilename() {
        return filename;
    }


}

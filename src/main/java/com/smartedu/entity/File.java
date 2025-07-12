package com.smartedu.entity;

public class File {
    Long id;
    String filename;
    String fileUrl;
    String description;
    Long taskId;
    Long courseId;
    Long studentId;
    Long submissionId;
    String tag;
    Double finished;

    public void setFinished(Double finished) {
        this.finished = finished;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFilename() {
        return filename;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getTag() {
        return tag;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public Double getFinished() {
        return finished;
    }
}

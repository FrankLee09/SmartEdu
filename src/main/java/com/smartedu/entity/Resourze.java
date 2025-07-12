package com.smartedu.entity;

import java.util.Date;

public class Resourze {
    Long id;
    Long courseId;
    String title;
    String uploadTime;
    String fileUrl;

    String tag;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getTag() {
        return tag;
    }

    public Long getCourseId() {
        return courseId;
    }
}

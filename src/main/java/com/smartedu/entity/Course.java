package com.smartedu.entity;


import java.util.List;

public class Course {
    private Long id;
    private String title;
    private String description;
    private Long teacherId;
    private String imgs;
    private String time;
    private String tag;
    private String teacherName;
    private String teacherAvatar;
    List<File> files;

    public void setFiles(List<File> files) {
        this.files = files;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setTeacherAvatar(String teacherAvatar) {
        this.teacherAvatar = teacherAvatar;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImgs(String imgs) {
        this.imgs = imgs;
    }
    public String getTitle() {
        return title;
    }
    public Long getId() {
        return id;
    }
    public Long getTeacherId() {
        return teacherId;
    }
    public String getDescription() {
        return description;
    }
    public String getImgs() {
        return imgs;
    }
    public String getTime() {
        return time;
    }
    public String getTag() {
        return tag;
    }
    public String getTeacherAvatar() {
        return teacherAvatar;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public List<File> getFiles() {
        return files;
    }
}

package com.smartedu.entity;

public class Clazz {

    private Long id;
    private String name;
    private Long teacherId;
    public void assginTeacher(Long tescherId){}

    public void updateInfo(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

}

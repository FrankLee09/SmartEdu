package com.smartedu.entity;

import java.util.List;

public class Student extends Account{
    private Long id;
    private String username;
    private String password;
    private String newPassword;
    private String role;
    private String name;
    private String email;
    private Long classId;
    private  String avatar;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }




    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Long getClassId() {
        return classId;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }
}

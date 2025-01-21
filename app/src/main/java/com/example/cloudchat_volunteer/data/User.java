package com.example.cloudchat_volunteer.data;

public class User {
    private String name;
    private String grade;
    private String hobby;

    public User(String name, String grade, String hobby) {
        this.name = name;
        this.grade = grade;
        this.hobby = hobby;
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }

    public String getHobby() {
        return hobby;
    }
}

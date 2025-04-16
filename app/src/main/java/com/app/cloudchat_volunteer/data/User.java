package com.app.cloudchat_volunteer.data;

public class User {
    private String name;
    private String grade;
    private String hobby;
    private String diary;

    public User(String name, String grade, String hobby,String diary) {
        this.name = name;
        this.grade = grade;
        this.hobby = hobby;
        this.diary = diary;
    }

    public String getName() {
        return name;
    }
    public  String getDiary(){
        return diary;
    }

    public String getGrade() {
        return grade;
    }

    public String getHobby() {
        return hobby;
    }
}

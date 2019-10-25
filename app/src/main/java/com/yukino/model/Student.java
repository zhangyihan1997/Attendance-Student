package com.yukino.model;

public class Student {
    private int student_id;
    private String password;

    public Student(int student_id, String password) {
        this.student_id = student_id;
        this.password = password;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

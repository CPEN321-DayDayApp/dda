package com.example.daydayapp.model;

public class ToDoModel {
    private int status,
                id;
    private String task,
                   date,
                   duration;

    public ToDoModel(int status, String task, String date, String duration, int id) {
        this.status = status;
        this.task = task;
        this.date = date;
        this.duration = duration;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}

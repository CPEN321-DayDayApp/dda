package com.example.daydayapp.model;


import com.example.daydayapp.enums.FriendStatus;

public class FriendModel {
    private String email = "",
                   status = "INACTIVE",
                   name,
                   id;

    public String getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        if (status == FriendStatus.INACTIVE)
            this.status = "INACTIVE";
        else
            this.status = "STUDYING";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email= email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

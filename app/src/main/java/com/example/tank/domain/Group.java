package com.example.tank.domain;

import java.util.List;

public class Group {
    String id;
    String name;
    List<String> keyModuls;
    List<String> emails;
    String admin;
    String urlImg;

    public Group(){

    }

    public Group(String id, String name,  List<String> emails, String admin, String urlImg,List<String> keyModuls) {
        this.id = id;
        this.name = name;
        this.keyModuls = keyModuls;
        this.emails = emails;
        this.admin = admin;
        this.urlImg = urlImg;
    }
    public Group(String id, String name,  List<String> emails, String admin, List<String> keyModuls) {
        this.id = id;
        this.name = name;
        this.keyModuls = keyModuls;
        this.emails = emails;
        this.admin = admin;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getKeyModuls() {
        return keyModuls;
    }

    public void setKeyModuls(List<String> keyModuls) {
        this.keyModuls = keyModuls;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}

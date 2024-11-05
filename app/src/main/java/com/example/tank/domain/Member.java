package com.example.tank.domain;

import java.util.ArrayList;
import java.util.List;

import java.util.List;

public class Member {
    private String email;
    private String img;
    private String name;
    private List<String> groups;
    private String idModule;


    public Member() {
    }
    public Member(String module, List<String> groups) {
        this.idModule = module;
        this.groups = groups;
    }
    public Member(String email) {
        this.email = email;
    }

    public Member(String email, String img, String name, List<String> groups, String idModule) {
        this.email = email;
        this.img = img;
        this.name = name;
        this.groups = groups;
        this.idModule = idModule;
    }
    public Member(String email, String img, String name) {
        this.email = email;
        this.img = img;
        this.name = name;

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }



    public String getIdModule() {
        return idModule;
    }

    public void setIdModule(String idModule) {
        this.idModule = idModule;
    }
}

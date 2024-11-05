package com.example.tank.domain;

public class MemberGroup {
    String name;
    String img;
    String rol;

    public MemberGroup(String name, String img, String rol) {
        this.name = name;
        this.img = img;
        this.rol = rol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}

package com.mobiapp4u.pc.routinebasketadmin.Modal;

public class Category {
    private String name;
    private String link;
    private String admin;

    public Category() {
    }

    public Category(String name, String link, String admin) {
        this.name = name;
        this.link = link;
        this.admin = admin;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

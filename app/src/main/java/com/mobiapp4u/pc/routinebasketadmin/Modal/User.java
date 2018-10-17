package com.mobiapp4u.pc.routinebasketadmin.Modal;

public class User {
    private String name;
    private String password;
    private String phone;
    private String IsStaff;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;

    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}


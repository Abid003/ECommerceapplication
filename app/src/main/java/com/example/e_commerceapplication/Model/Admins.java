package com.example.e_commerceapplication.Model;

public class Admins {

    private String Name, phone, password;

    public Admins() {
    }

    public Admins(String name, String phone, String password) {
        this.Name = name;
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = Name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

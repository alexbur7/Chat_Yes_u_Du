package com.example.myproject;

import android.util.Log;

public class User {
    private String name;
    private String surname;
    private String country;
    private String city;
    private String photo_url;
    private String uuid;
    private String region;
    private String sex;
    private String age;
    private String status;
    private static User currentUser;
    public User(){}
    public User(String uuid,String name, String surname, String country, String city,
                String photo_url, String region,String sex,String age, String status){
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.city = city;
        this.photo_url=photo_url;
        this.uuid=uuid;
        this.region = region;
        this.sex = sex;
        this.age = age;
        this.status = status;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser,String uuid) {
        User.currentUser = currentUser;
        if (currentUser!=null) {
            User.currentUser.setUuid(uuid);
            User.currentUser.setStatus("offline");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

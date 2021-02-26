package com.yes_u_du.zuyger.account;

public class User {
    private String name;
    private String surname;
    private String country;
    private String city;
    private String photo_url;
    private String photo_url1;
    private String photo_url2;
    private String photo_url3;
    private String uuid;
    private String region;
    private String sex;
    private String age;
    private String status;
    private String admin;
    private String admin_block;
    private String about;
    private String typing;
    private String perm_block;
    private String verified;
    private long dateBirthday;

    private long online_time;
    private static User currentUser;
    public User(){}
    public User(String uuid,String name, String surname, String country, String city,
                String photo_url, String region,String sex,String age, String status,
                String admin, long online_time , String admin_block,String about,
                String photo_url1, String photo_url2, String photo_url3, String typing, String perm_block, String verified, long dateBirthday){
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
        this.admin = admin;
        this.online_time = online_time;
        this.admin_block = admin_block;
        this.typing = typing;
        this.perm_block = perm_block;
        this.verified = verified;
        //changed
        this.about=about;
        this.photo_url1 = photo_url1;
        this.photo_url2 = photo_url2;
        this.photo_url3 = photo_url3;
        this.dateBirthday = dateBirthday;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser, String uuid, String string) {
        User.currentUser = currentUser;
        if (currentUser!=null) {
            User.currentUser.setUuid(uuid);
            User.currentUser.setStatus(string);
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public long getOnline_time() {
        return online_time;
    }

    public void setOnline_time(long online_time) {
        this.online_time = online_time;
    }

    public String getAdmin_block() {
        return admin_block;
    }

    public void setAdmin_block(String admin_block) {
        this.admin_block = admin_block;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoto_url1() {
        return photo_url1;
    }

    public void setPhoto_url1(String photo_url1) {
        this.photo_url1 = photo_url1;
    }

    public String getPhoto_url2() {
        return photo_url2;
    }

    public void setPhoto_url2(String photo_url2) {
        this.photo_url2 = photo_url2;
    }

    public String getPhoto_url3() {
        return photo_url3;
    }

    public void setPhoto_url3(String photo_url3) {
        this.photo_url3 = photo_url3;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public String getPerm_block() {
        return perm_block;
    }

    public void setPerm_block(String perm_block) {
        this.perm_block = perm_block;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public long getDateBirthday() {
        return dateBirthday;
    }

    public void setDateBirthday(long dateBirthday) {
        this.dateBirthday = dateBirthday;
    }
}

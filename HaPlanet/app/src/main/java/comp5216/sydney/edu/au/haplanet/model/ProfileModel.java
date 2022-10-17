package comp5216.sydney.edu.au.haplanet.model;

import java.util.Date;

public class ProfileModel {

    private String picture;
    private String uid;
    private String username;
    private String introduction;
//    private String gender;
    private int age;

    public ProfileModel(String picture, String uid, String username, String introduction, int age) {
        this.picture = picture;
        this.uid = uid;
        this.username = username;
        this.introduction = introduction;
//        this.gender = gender;
        this.age = age;
    }

    public ProfileModel() {
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String signature) {
        this.introduction = signature;
    }

//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}

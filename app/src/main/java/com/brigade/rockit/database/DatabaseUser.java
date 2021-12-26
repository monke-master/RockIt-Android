package com.brigade.rockit.database;

import com.brigade.rockit.data.User;

import java.util.ArrayList;

public class DatabaseUser {
    private String name;
    private String surname;
    private String email;
    private String login;
    private String profilePicture;
    private String bio;
    private ArrayList<String> posts;
    private ArrayList<String> music;
    private ArrayList<User> followingList;
    private ArrayList<User> followersList;
    private String phone;

    public DatabaseUser() {
        bio = "";
        profilePicture = "profile_pictures/default.jpg";
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public ArrayList<User> getFollowingList() {
        return followingList;
    }

    public ArrayList<User> getFollowersList() {
        return followersList;
    }

    public ArrayList<String> getMusic() {
        return music;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

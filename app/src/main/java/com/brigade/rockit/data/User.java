package com.brigade.rockit.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Objects;

public class User {
    private String email;
    private String name;
    private String surname;
    private String phone;
    private String login;
    private String bio;
    private Uri pictureUri;
    private String id;
    private ArrayList<String> followingList;
    private ArrayList<String> followersList;

    public User() {
        bio = "";
        followingList = new ArrayList<>();
        followersList = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(ArrayList<String> followingList) {
        this.followingList = followingList;
    }

    public ArrayList<String> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(ArrayList<String> followersList) {
        this.followersList = followersList;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

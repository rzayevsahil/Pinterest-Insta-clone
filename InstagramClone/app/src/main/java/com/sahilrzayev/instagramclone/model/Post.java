package com.sahilrzayev.instagramclone.model;

public class Post {
    public String email;
    public String comment;
    public String downloadUrl;
    public String profileImage;

    public Post(String email, String comment, String downloadUrl, String profileImage) {
        this.email = email;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
        this.profileImage = profileImage;
    }

}

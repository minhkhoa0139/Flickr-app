package vn.edu.usth.flickrapp.Model;

import java.io.Serializable;

public class Image implements Serializable {
    private String email;
    private String emailPhu;
    private String uri;
    private String likeCount;
    private String commentCount;
    private String content;
    private String name;
    private String type;

    // Constructors
    public Image() {
    }

    public Image(String email, String uri, String likeCount, String commentCount, String content, String name, String emailPhu, String Type) {
        this.email = email;
        this.uri = uri;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.content = content;
        this.name = name;
        this.emailPhu = emailPhu;
        this.type = Type;
    }

    // Getters và Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() { return type; }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmailPhu() {
        return emailPhu;
    }

    public void setEmailPhu(String emailPhu) {
        this.emailPhu = emailPhu;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }
}


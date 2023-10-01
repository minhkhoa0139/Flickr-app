package vn.edu.usth.flickrapp.Model;

import kotlin.text.UStringsKt;

public class NewsItem {
    private String username;
    private String imageResource;
    private String avaResource;
    private int likeCount;
    private int commentCount;

    public NewsItem(String username, String imageResource, String avaResource, int likeCount, int commentCount) {
        this.username = username;
        this.imageResource = imageResource;
        this.avaResource = avaResource;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public String getUsername() {
        return username;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getAvaResource() {
        return avaResource;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }
}


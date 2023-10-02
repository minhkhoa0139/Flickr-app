package vn.edu.usth.flickrapp.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String email;
    private String name;
    private String comment;
    private String timestamp;
    private String linkUri;

    // Constructors
    public Comment() {
        // Default constructor required for Firebase Realtime Database
    }

    public Comment(String email, String comment, String linkUri) {
        this.email = email;
        this.comment = comment;
        this.linkUri = linkUri;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.timestamp = sdf.format(new Date());
    }

    // Getters and Setters
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLinkUri() {
        return linkUri;
    }

    public void setLinkUri(String linkUri) {
        this.linkUri = linkUri;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

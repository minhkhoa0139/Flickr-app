package vn.edu.usth.flickrapp.Model;

public class Notification {
    private String content;
    private String email;
    private String emailPhu;
    private String Uri;

    public Notification(String content, String email, String emailPhu, String Uri) {
        this.content = content;
        this.email = email;
        this.emailPhu = emailPhu;
        this.Uri = Uri;
    }

    public String getUri() {
        return Uri;
    }


    public void setUri(String Uri) {
        this.content = Uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPhu() {
        return emailPhu;
    }

    public void setEmailPhu(String emailPhu) {
        this.emailPhu = emailPhu;
    }
}


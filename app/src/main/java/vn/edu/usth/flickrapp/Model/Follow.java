package vn.edu.usth.flickrapp.Model;

public class Follow {
    private String email;
    private String emailPhu;
    private String followed;

    public Follow() {
    }

    public Follow(String email, String emailPhu, String followed) {
        this.email = email;
        this.emailPhu = emailPhu;
        this.followed = followed;
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

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        followed = followed;
    }
}

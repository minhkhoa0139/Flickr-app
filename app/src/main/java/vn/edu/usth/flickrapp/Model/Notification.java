package vn.edu.usth.flickrapp.Model;

public class Notification {
    private int avatarResId;
    private String content;
    private String email;
    private String emailPhu;
    private int otherImageResId;
    private String nameEmailPhu;

    public Notification(int avatarResId, int otherImageResId, String content, String emailChu, String emailPhu) {
        this.avatarResId = avatarResId;
        this.otherImageResId = otherImageResId;
        this.content = content;
        this.email = emailChu;
        this.emailPhu = emailPhu;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public int getOtherImageResId() {
        return otherImageResId;
    }

    public void setOtherImageResId(int otherImageResId) {
        this.otherImageResId = otherImageResId;
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public String getNameEmailPhu() {
        return nameEmailPhu;
    }

    public void setNameEmailPhu(String nameEmailPhu) {
        this.nameEmailPhu = nameEmailPhu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailChu) {
        this.email = emailChu;
    }

    public String getEmailPhu() {
        return emailPhu;
    }

    public void setEmailPhu(String emailPhu) {
        this.emailPhu = emailPhu;
    }
}


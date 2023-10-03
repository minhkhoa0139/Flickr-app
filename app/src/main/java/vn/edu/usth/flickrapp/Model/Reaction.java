package vn.edu.usth.flickrapp.Model;

public class Reaction {
    private String uri;
    private String liked;
    private String email;

    public Reaction(String uri, String liked, String email) {
        this.uri = uri;
        this.liked = liked;
        this.email = email;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String GetLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

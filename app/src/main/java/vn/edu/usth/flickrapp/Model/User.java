package vn.edu.usth.flickrapp.Model;

import java.io.Serializable;

public class User implements Serializable {
    public String firstName;
    public String lastName;
    public String birthday;
    public String email;
    public String password;

    public User() {
    }

    public User(String firstName, String lastName, String birthday, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.email = email;
        this.password = password;
    }
}
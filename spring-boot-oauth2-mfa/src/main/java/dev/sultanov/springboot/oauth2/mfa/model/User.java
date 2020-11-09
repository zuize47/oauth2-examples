package dev.sultanov.springboot.oauth2.mfa.model;

public class User {
    long id;
    String username;
    String password;
    boolean vinaone;
    Integer available;
    int status;

    // Role role;
    String name;

    public User(long id, String username, String password, boolean vinaone, int available, int status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.vinaone = vinaone;
        this.available = available;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVinaone() {
        return vinaone;
    }

    public int getAvailable() {
        return available;
    }

    public int getStatus() {
        return status;
    }

    // public Role getRole() {
    //     return role;
    // }

    // public void setRole(Role role) {
    //     this.role = role;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
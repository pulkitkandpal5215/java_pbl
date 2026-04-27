package com.examgui.model;

import java.util.UUID;

public class User {
    public enum Role { STUDENT, ADMIN }

    private String id;
    private String name, email, password;
    private Role role;

    public User(String name, String email, String password, Role role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Constructor for loading from database
    public User(String id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getId()       { return id; }
    public void setId(String id) { this.id = id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role getRole()       { return role; }
    public boolean isAdmin()    { return role == Role.ADMIN; }
}

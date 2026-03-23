package com.examgui.model;

import java.time.LocalDateTime;
import java.util.*;

// ── User ──────────────────────────────────────────────────────────────────────
public class User {
    public enum Role { STUDENT, ADMIN }

    private static int nextId = 1;
    private final int id;
    private String name, email, password;
    private Role role;

    public User(String name, String email, String password, Role role) {
        this.id = nextId++;
        this.name = name; this.email = email;
        this.password = password; this.role = role;
    }

    public int getId()        { return id; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getPassword(){ return password; }
    public Role getRole()     { return role; }
    public boolean isAdmin()  { return role == Role.ADMIN; }
}

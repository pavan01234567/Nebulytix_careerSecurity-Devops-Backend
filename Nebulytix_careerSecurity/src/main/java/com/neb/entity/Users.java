package com.neb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

import com.neb.constants.Role;

@Entity
@Table(name = "users")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // typically email

    @Column(nullable = false)
    private String password;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(nullable = false)
    private boolean enabled = true;

    // Allow multiple roles per user
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    // Optional account control fields
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "failed_login_count")
    private int failedLoginCount = 0;

    public Users() {}

    public Users(String email, String password, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles == null ? new HashSet<>() : roles;
    }
    public void addRole(Role role) { this.roles.add(role); }
    public void removeRole(Role role) { this.roles.remove(role); }
}

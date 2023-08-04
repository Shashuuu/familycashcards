package com.example.cashcard.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Entity
public class CashCardUser {

    @Id
    private String username;
    private String password;
    private String roles;

    protected CashCardUser() {}

    public CashCardUser(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = "ROLE_OWNER";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "CashCardUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashCardUser that = (CashCardUser) o;
        return username.equals(that.username) && Objects.equals(password, that.password) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, roles);
    }
}

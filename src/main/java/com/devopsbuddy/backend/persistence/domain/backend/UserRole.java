package com.devopsbuddy.backend.persistence.domain.backend;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by root on 08/06/17.
 */
@Entity
@Table(name = "user_role")
public class UserRole implements Serializable {
    /** The serial versionUID for Serializable classes */
    private static final long serialVersionUID = 1L;

    /** Default constructor */
    public UserRole(){

    }

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserRole userRole = (UserRole) obj;

        if (!user.equals(userRole.user)) return false;

        return role.equals(userRole.role);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result * role.hashCode();
        return result;
    }
}

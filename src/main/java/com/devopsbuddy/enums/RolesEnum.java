package com.devopsbuddy.enums;

/**
 * Defines possible roles
 * Created by root on 10/06/17.
 */
public enum RolesEnum {

    /** Spring security uses the 'ROLE_' as a default prefix */
    BASIC(1, "ROLE_BASIC"),
    PRO(2, "ROLE_PRO"),
    ADMIN(3, "ROLE_ADMIN");

    private final int id;

    private final String roleName;

    RolesEnum(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
}

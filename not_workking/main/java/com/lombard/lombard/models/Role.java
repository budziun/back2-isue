package com.lombard.lombard.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "permissions_level", nullable = false)
    private Integer permissionsLevel;

    @Column(name = "max_buy")
    private Integer maxBuy;

    public Role() {
    }

    public Role(Integer id, String roleName, String description, Integer permissionsLevel, Integer maxBuy) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.permissionsLevel = permissionsLevel;
        this.maxBuy = maxBuy;
    }

    public Integer getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPermissionsLevel() {
        return permissionsLevel;
    }

    public void setPermissionsLevel(Integer permissionsLevel) {
        this.permissionsLevel = permissionsLevel;
    }

    public Integer getMaxBuy() {
        return maxBuy;
    }

    public void setMaxBuy(Integer maxBuy) {
        this.maxBuy = maxBuy;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                ", permissionsLevel=" + permissionsLevel +
                ", maxBuy=" + maxBuy +
                '}';
    }
}
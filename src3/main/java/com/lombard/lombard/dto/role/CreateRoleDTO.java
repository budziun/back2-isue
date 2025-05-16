package com.lombard.lombard.dto.role;

public class CreateRoleDTO {
    private String roleName;
    private String description;
    private Integer permissionsLevel;
    private Integer maxBuy;

    // Getters and Setters
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
}
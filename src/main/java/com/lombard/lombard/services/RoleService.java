package com.lombard.lombard.services;

import com.lombard.lombard.models.Role;
import com.lombard.lombard.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }


    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }


    public RoleResponse createRole(Role role) {
        try {
            if (roleRepository.existsByRoleName(role.getRoleName())) {
                return new RoleResponse(null, "role already exists");
            }

            if (role.getPermissionsLevel() == null) {
                return new RoleResponse(null, "permissions level required");
            }

            Role savedRole = roleRepository.save(role);
            return new RoleResponse(savedRole, null);

        } catch (Exception e) {
            return new RoleResponse(null, e.getMessage());
        }
    }


    public RoleResponse updateRole(Integer id, Role roleDetails) {
        Optional<Role> roleOpt = roleRepository.findById(id);

        if (roleOpt.isEmpty()) {
            return new RoleResponse(null, "Role not found");
        }

        Role existingRole = roleOpt.get();

        try {
            if (!existingRole.getRoleName().equals(roleDetails.getRoleName()) &&
                    roleRepository.existsByRoleName(roleDetails.getRoleName())) {
                return new RoleResponse(null, "role already exists");
            }

            // Update fields
            existingRole.setRoleName(roleDetails.getRoleName());
            existingRole.setDescription(roleDetails.getDescription());
            existingRole.setPermissionsLevel(roleDetails.getPermissionsLevel());
            existingRole.setMaxBuy(roleDetails.getMaxBuy());

            Role updatedRole = roleRepository.save(existingRole);
            return new RoleResponse(updatedRole, null);

        } catch (Exception e) {
            return new RoleResponse(null, e.getMessage());
        }
    }


    public DeleteRoleResponse deleteRole(Integer id) {
        return roleRepository.findById(id)
                .map(role -> {
                    try {
                        roleRepository.delete(role);
                        return new DeleteRoleResponse(true, null);
                    } catch (DataIntegrityViolationException e) {
                        return new DeleteRoleResponse(false, "cannot delete role");
                    } catch (Exception e) {
                        return new DeleteRoleResponse(false, e.getMessage());
                    }
                })
                .orElse(new DeleteRoleResponse(false, "role not found"));
    }


    public static class RoleResponse {
        private final Role role;
        private final String errorMessage;

        public RoleResponse(Role role, String errorMessage) {
            this.role = role;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return role != null;
        }

        public Role getRole() {
            return role;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


    public static class DeleteRoleResponse {
        private final boolean deleted;
        private final String errorMessage;

        public DeleteRoleResponse(boolean deleted, String errorMessage) {
            this.deleted = deleted;
            this.errorMessage = errorMessage;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
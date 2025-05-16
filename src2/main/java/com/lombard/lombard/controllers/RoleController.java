package com.lombard.lombard.controllers;


import com.lombard.lombard.dto.role.CreateRoleDTO;
import com.lombard.lombard.dto.role.RoleDTO;
import com.lombard.lombard.dto.role.UpdateRoleDTO;
import com.lombard.lombard.models.Role;
import com.lombard.lombard.services.RoleService;
import com.lombard.lombard.services.RoleService.RoleResponse;
import com.lombard.lombard.services.RoleService.DeleteRoleResponse;
import com.lombard.lombard.utils.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RoleController {

    private final RoleService roleService;
    private final Mapper mapper;

    @Autowired
    public RoleController(RoleService roleService, Mapper mapper) {
        this.roleService = roleService;
        this.mapper = mapper;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDTO> roleDTOs = mapper.toRoleDTOList(roles);
        return ResponseEntity.ok(roleDTOs);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id)
                .map(role -> ResponseEntity.ok(mapper.toRoleDTO(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/roles/name/{roleName}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String roleName) {
        return roleService.getRoleByName(roleName)
                .map(role -> ResponseEntity.ok(mapper.toRoleDTO(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody CreateRoleDTO roleDTO) {
        Role role = mapper.toRole(roleDTO);
        RoleResponse response = roleService.createRole(role);

        if (response.isSuccess()) {
            return new ResponseEntity<>(mapper.toRoleDTO(response.getRole()), HttpStatus.CREATED);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", response.getErrorMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody UpdateRoleDTO roleDTO) {
        return roleService.getRoleById(id)
                .map(existingRole -> {
                    mapper.updateRoleFromDTO(existingRole, roleDTO);

                    RoleResponse response = roleService.updateRole(id, existingRole);

                    if (response.isSuccess()) {
                        return ResponseEntity.ok(mapper.toRoleDTO(response.getRole()));
                    } else {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", response.getErrorMessage());
                        return ResponseEntity.badRequest().body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Integer id) {
        DeleteRoleResponse response = roleService.deleteRole(id);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("deleted", response.isDeleted());

        if (!response.isDeleted() && response.getErrorMessage() != null) {
            responseMap.put("error", response.getErrorMessage());

            if (response.getErrorMessage().equals("Role not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
            }
        }

        return ResponseEntity.ok(responseMap);
    }
}
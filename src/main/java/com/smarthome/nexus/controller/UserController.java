package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.request.CreateUserReq;
import com.smarthome.nexus.dto.request.UpdateUserReq;
import com.smarthome.nexus.dto.response.UserResDTO;
import com.smarthome.nexus.entity.Role;
import com.smarthome.nexus.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResDTO> createUser(@Valid @RequestBody CreateUserReq request) {
        log.info("REST request to create user: {}", request.getEmail());
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<UserResDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        log.info("REST request to get users page: {}, size: {}, search: {}", page, size, search);
        return ResponseEntity.ok(userService.getAllUsers(page, size, sortBy, sortDir, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResDTO> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserReq request) {
        log.info("REST request to update user id: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResDTO> changeUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        log.info("REST request to change user role id: {}, role: {}", id, role);
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

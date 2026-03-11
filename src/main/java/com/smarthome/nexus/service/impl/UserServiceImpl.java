package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.CreateUserReq;
import com.smarthome.nexus.dto.request.UpdateUserReq;
import com.smarthome.nexus.dto.response.UserResDTO;
import com.smarthome.nexus.entity.Role;
import com.smarthome.nexus.entity.User;
import com.smarthome.nexus.exception.BadRequestException;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.UserRepository;
import com.smarthome.nexus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResDTO createUser(CreateUserReq request) {
        log.info("Creating new user with email: {}", request.getEmail());
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Email already in use: {}", request.getEmail());
            throw new BadRequestException("Email already in use");
        }

        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.MEMBER);
        }
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return modelMapper.map(savedUser, UserResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResDTO getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResDTO> getAllUsers(int page, int size, String sortBy, String sortDir, String search) {
        log.debug("Fetching users page: {}, size: {}, search: {}", page, size, search);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users;
        if (search != null && !search.isEmpty()) {
            users = userRepository.searchUsers(search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(user -> modelMapper.map(user, UserResDTO.class));
    }

    @Override
    @Transactional
    public UserResDTO updateUser(Long id, UpdateUserReq request) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                log.warn("Attempt to change email to one already in use: {}", request.getEmail());
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            if (user.getRole() == Role.ADMIN && request.getRole() != Role.ADMIN) {
                long adminCount = userRepository.countByRoleAndStatus(Role.ADMIN, "ACTIVE");
                if (adminCount <= 1 && "ACTIVE".equals(user.getStatus())) {
                    log.warn("Failed to demote the last active admin with ID: {}", id);
                    throw new BadRequestException("Cannot demote the last active admin user");
                }
            }
            user.setRole(request.getRole());
        }

        if (request.getStatus() != null) {
            if (user.getRole() == Role.ADMIN && "ACTIVE".equals(user.getStatus())
                    && !"ACTIVE".equals(request.getStatus())) {
                long adminCount = userRepository.countByRoleAndStatus(Role.ADMIN, "ACTIVE");
                if (adminCount <= 1) {
                    log.warn("Failed to deactivate the last active admin with ID: {}", id);
                    throw new BadRequestException("Cannot deactivate the last active admin user");
                }
            }
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        log.info("User with ID: {} updated successfully", id);
        return modelMapper.map(updatedUser, UserResDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Safe delete validation
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRoleAndStatus(Role.ADMIN, "ACTIVE");
            if (adminCount <= 1 && "ACTIVE".equals(user.getStatus())) {
                log.warn("Failed to delete the last active admin with ID: {}", id);
                throw new BadRequestException("Cannot delete the last active admin user");
            }
        }

        user.setStatus("INACTIVE"); // Soft delete
        userRepository.save(user);
        log.info("User with ID: {} marked as INACTIVE", id);
    }

    @Override
    @Transactional
    public UserResDTO changeUserRole(Long id, Role newRole) {
        log.info("Changing role for user ID: {} to {}", id, newRole);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userRepository.countByRoleAndStatus(Role.ADMIN, "ACTIVE");
            if (adminCount <= 1 && "ACTIVE".equals(user.getStatus())) {
                log.warn("Failed to change role of the last active admin with ID: {}", id);
                throw new BadRequestException("Cannot change role of the last active admin user");
            }
        }

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        log.info("Role for user ID: {} changed to {} successfully", id, newRole);
        return modelMapper.map(updatedUser, UserResDTO.class);
    }
}

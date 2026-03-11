package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateUserReq;
import com.smarthome.nexus.dto.request.UpdateUserReq;
import com.smarthome.nexus.dto.response.UserResDTO;
import com.smarthome.nexus.entity.Role;
import com.smarthome.nexus.entity.User;
import com.smarthome.nexus.exception.BadRequestException;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.UserRepository;
import com.smarthome.nexus.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private CreateUserReq createReq;
    private UpdateUserReq updateReq;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("encoded_pass")
                .role(Role.MEMBER)
                .status("ACTIVE")
                .build();

        createReq = new CreateUserReq("Test User", "test@example.com", "password123", Role.MEMBER, "ACTIVE");
        updateReq = new UpdateUserReq("New Name", null, null, null, null);
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResDTO result = userService.createUser(createReq);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsBadRequestException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.createUser(createReq));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResDTO result = userService.updateUser(1L, updateReq);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertEquals("INACTIVE", user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void deleteLastAdmin_ThrowsBadRequestException() {
        user.setRole(Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRoleAndStatus(Role.ADMIN, "ACTIVE")).thenReturn(1L);

        assertThrows(BadRequestException.class, () -> userService.deleteUser(1L));
    }
}

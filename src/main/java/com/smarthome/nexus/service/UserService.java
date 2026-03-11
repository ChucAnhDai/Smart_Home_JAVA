package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateUserReq;
import com.smarthome.nexus.dto.request.UpdateUserReq;
import com.smarthome.nexus.dto.response.UserResDTO;
import com.smarthome.nexus.entity.Role;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResDTO createUser(CreateUserReq request);

    UserResDTO getUserById(Long id);

    Page<UserResDTO> getAllUsers(int page, int size, String sortBy, String sortDir, String search);

    UserResDTO updateUser(Long id, UpdateUserReq request);

    void deleteUser(Long id);

    UserResDTO changeUserRole(Long id, Role newRole);
}

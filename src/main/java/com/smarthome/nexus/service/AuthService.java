package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.AuthenticationRequest;
import com.smarthome.nexus.dto.request.RegisterRequest;
import com.smarthome.nexus.dto.response.AuthenticationResponse;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}

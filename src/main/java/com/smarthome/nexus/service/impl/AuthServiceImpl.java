package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.AuthenticationRequest;
import com.smarthome.nexus.dto.request.RegisterRequest;
import com.smarthome.nexus.dto.response.AuthenticationResponse;
import com.smarthome.nexus.entity.Role;
import com.smarthome.nexus.entity.User;
import com.smarthome.nexus.repository.UserRepository;
import com.smarthome.nexus.security.JwtTokenProvider;
import com.smarthome.nexus.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;

        public AuthServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
                this.repository = repository;
                this.passwordEncoder = passwordEncoder;
                this.jwtTokenProvider = jwtTokenProvider;
                this.authenticationManager = authenticationManager;
        }

        public AuthenticationResponse register(RegisterRequest request) {
                if (repository.findByEmail(request.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("Email is already taken.");
                }

                var user = User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.MEMBER)
                                .status("ACTIVE")
                                .build();
                repository.save(user);
                var jwtToken = jwtTokenProvider.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .message("User registered successfully")
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .build();
        }

        @Override
        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtTokenProvider.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .message("User logged in successfully")
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .build();
        }
}

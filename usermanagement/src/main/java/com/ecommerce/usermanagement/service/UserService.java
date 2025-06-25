package com.ecommerce.usermanagement.service;

import com.ecommerce.usermanagement.dto.UserRegistrationDto;
import com.ecommerce.usermanagement.dto.UserResponseDto;
import com.ecommerce.usermanagement.model.User;
import com.ecommerce.usermanagement.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponseDto registerUser(UserRegistrationDto registrationDto);

    UserResponseDto getUserById(Long id);

    UserResponseDto getUserByEmail(String email);

    List<UserResponseDto> getAllUsers();

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto updateUser(Long id, UserRegistrationDto updateDto);

    UserResponseDto updateUserStatus(Long id, UserStatus status);

    void deleteUser(Long id);

    List<UserResponseDto> getUsersByStatus(UserStatus status);

    List<UserResponseDto> searchUsersByName(String name);

    List<UserResponseDto> getUsersByCity(String city);

    long getUserCountByStatus(UserStatus status);

    boolean existsByEmail(String email);
}

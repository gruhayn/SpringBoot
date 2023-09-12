package com.unitech.api.service;

import com.unitech.api.dto.UserDto;
import com.unitech.api.dto.UserResponse;
import com.unitech.api.models.UserEntity;

import java.util.Optional;

public interface RegistrationService {
    UserDto createRegistration(UserDto userDto);
    boolean isRegistrationExist(UserDto userDto);

    UserResponse getAllRegistration(int pageNo, int pageSize);

    Optional<UserEntity> findByPin(String pin);
}

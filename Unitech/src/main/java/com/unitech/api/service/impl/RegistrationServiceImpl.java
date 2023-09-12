package com.unitech.api.service.impl;

import com.unitech.api.constants.Constants;
import com.unitech.api.dto.UserDto;
import com.unitech.api.dto.UserResponse;
import com.unitech.api.exceptions.RegistrationAlreadyExistsException;
import com.unitech.api.models.UserEntity;
import com.unitech.api.repository.RegistrationRepository;
import com.unitech.api.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private RegistrationRepository registrationRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationServiceImpl(RegistrationRepository registrationRepository, PasswordEncoder passwordEncoder) {
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createRegistration(UserDto userDto) {

        if (isRegistrationExist(userDto))
        {
            throw new RegistrationAlreadyExistsException("Registration already exist");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setPin(userDto.getPin());
        userEntity.setPassword(passwordEncoder.encode((userDto.getPassword())));
        userEntity.setStatusId(Constants.UserStatuses.Active.id);
        userEntity.setBalance(0.0);

        UserEntity newUserEntity = registrationRepository.save(userEntity);

        UserDto response = new UserDto();
        response.setId(newUserEntity.getId());
        response.setPin(newUserEntity.getPin());
        response.setPassword(newUserEntity.getPassword());
        return response;
    }

    @Override
    public boolean isRegistrationExist(UserDto userDto)
    {
        return registrationRepository.existsByPin(userDto.getPin());
    }


    @Override
    public UserResponse getAllRegistration(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<UserEntity> registrations = registrationRepository
                .findByStatusId(Constants.UserStatuses.Active.id, pageable);

        List<UserEntity> listOfRegistration = registrations.getContent();
        List<UserDto> content = listOfRegistration.stream().map(p -> mapToDto(p)).collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(content);
        userResponse.setPageNo(registrations.getNumber());
        userResponse.setPageSize(registrations.getSize());
        userResponse.setTotalElements(registrations.getTotalElements());
        userResponse.setTotalPages(registrations.getTotalPages());
        userResponse.setLast(registrations.isLast());

        return userResponse;
    }

    @Override
    public Optional<UserEntity> findByPin(String pin){
        return registrationRepository.findByPin(pin);
    }

    private UserDto mapToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setPin(userEntity.getPin());
        userDto.setPassword(userEntity.getPassword());

        return userDto;
    }

}

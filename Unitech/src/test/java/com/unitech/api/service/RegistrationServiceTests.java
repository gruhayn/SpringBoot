package com.unitech.api.service;

import com.unitech.api.constants.Constants;
import com.unitech.api.dto.UserDto;
import com.unitech.api.dto.UserResponse;
import com.unitech.api.exceptions.RegistrationAlreadyExistsException;
import com.unitech.api.models.UserEntity;
import com.unitech.api.repository.RegistrationRepository;
import com.unitech.api.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegistrationServiceTests {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    public void RegistrationService_CreateRegistration_ReturnsRegistrationDto() {
        UserEntity userEntity = UserEntity.builder()
                .pin("pin1")
                .password("pass1")
                .statusId(Constants.UserStatuses.Active.id).build();

        UserDto userDto = UserDto.builder()
                .pin("pin1")
                .password("pass1").build();


        when(registrationRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(userEntity);



        UserDto saved = registrationService.createRegistration(userDto);

        assertThat(saved).isNotNull();
    }

    @Test
    public void RegistrationService_CreateSameRegistrationTwice_ThrowsRegistrationAlreadyExistsException() {
        UserEntity userEntity = UserEntity.builder()
                .pin("pin1")
                .password("pass1").build();
        UserDto userDto = UserDto.builder()
                .pin("pin1")
                .password("pass1").build();

        when(registrationRepository.existsByPin(Mockito.any(String.class)))
                .thenReturn(true);

        assertThrows(RegistrationAlreadyExistsException.class, () -> {
            registrationService.createRegistration(userDto);
        });
    }

    @Test
    public void RegistrationService_IsRegistrationExist_DoesntExistReturnsFalse() {
        UserDto userDto = UserDto.builder()
                .pin("pin1")
                .password("pass1").build();

        when(registrationRepository.existsByPin(Mockito.any(String.class)))
                .thenReturn(false);

        assertThat(registrationService.isRegistrationExist(userDto)).isEqualTo(false);
    }

    @Test
    public void RegistrationService_IsRegistrationExist_ExistReturnsTrue() {
        UserDto userDto = UserDto.builder()
                .pin("pin1")
                .password("pass1").build();

        when(registrationRepository.existsByPin(Mockito.any(String.class)))
                .thenReturn(true);

        assertThat(registrationService.isRegistrationExist(userDto)).isEqualTo(true);
    }


    @Test
    public void RegistrationService_GetAllRegistration_ReturnsResponseDto() {
        Page<UserEntity> registrations = Mockito.mock(Page.class);

        when(registrationRepository.findByStatusId(Mockito.any(int.class),Mockito.any(Pageable.class)))
            .thenReturn(registrations);

        UserResponse savedRegistrations = registrationService.getAllRegistration(1,10);

        Assertions.assertThat(savedRegistrations).isNotNull();
    }

    @Test
    public void RegistrationService_FindByUser_ExistsReturnsUserDto() {

        UserEntity userEntity = UserEntity.builder()
                .pin("pin1")
                .password("pass1").build();

        Optional<UserEntity> optionalUserEntity = Optional.of(userEntity);

        UserDto userDto = UserDto.builder()
                .pin("pin1")
                .password("pass1").build();

        when(registrationRepository.findByPin(Mockito.any(String.class)))
                .thenReturn(optionalUserEntity);

        Optional<UserEntity> dto = registrationService.findByPin(userDto.getPin());

        Assertions.assertThat(dto).isNotEmpty();
    }


    @Test
    public void RegistrationService_FindByUser_DoesntExistReturnsEmpty() {


        UserDto userDto = UserDto.builder()
                .pin("pin1")
                .password("pass1").build();

        when(registrationRepository.findByPin(Mockito.any(String.class)))
                .thenReturn(Optional.empty());

        Optional<UserEntity> dto = registrationService.findByPin(userDto.getPin());

        Assertions.assertThat(dto).isEmpty();
    }



}

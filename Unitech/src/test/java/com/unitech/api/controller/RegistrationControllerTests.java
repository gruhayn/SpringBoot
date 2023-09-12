package com.unitech.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitech.api.controllers.RegistrationController;
import com.unitech.api.dto.UserDto;
import com.unitech.api.dto.UserResponse;
import com.unitech.api.exceptions.RegistrationAlreadyExistsException;
import com.unitech.api.models.UserEntity;
import com.unitech.api.service.RegistrationService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegistrationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity userEntity;
    private UserDto userDto;


    @BeforeEach
    public void init() {
        userEntity = UserEntity.builder()
                .pin("pin2")
                .password("pass2").build();
        userDto = UserDto.builder()
                .pin("pin2")
                .password("pass2").build();
    }



    @Test
    public void RegistrationController_GetAllRegistration_ReturnResponseDto() throws Exception {
        UserResponse responseDto = UserResponse.builder()
                .pageSize(10)
                .last(true)
                .pageNo(1)
                .content(Arrays.asList(userDto))
                .build();

        when(registrationService.getAllRegistration(1,10))
                .thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/api/registration/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo","1")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath(
                                        "$.content.size()",
                                        CoreMatchers.is(responseDto.getContent().size()
                                        )
                                )
                );
    }

    @Test
    public void RegistrationController_CreateRegistration_ReturnCreated() throws Exception {
        given(registrationService.createRegistration(ArgumentMatchers.any()))
                .willAnswer((invocation -> invocation.getArgument(0)));

        ResultActions response = mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void RegistrationController_CreateRegistration_UserAlreadyExistReturnBadRequest() throws Exception {
        given(registrationService.createRegistration(ArgumentMatchers.any()))
                .willThrow(new RegistrationAlreadyExistsException("Pin already exists"));

        ResultActions response = mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}

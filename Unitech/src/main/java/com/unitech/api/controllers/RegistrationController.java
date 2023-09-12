package com.unitech.api.controllers;


import com.unitech.api.dto.UserDto;
import com.unitech.api.dto.UserResponse;
import com.unitech.api.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private RegistrationService registrationService;

    private AuthenticationManager authenticationManager;

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getPin(),
                        userDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User logged in", HttpStatus.OK);
    }

    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthenticationManager authenticationManager) {
        this.registrationService = registrationService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("getAll")
    public ResponseEntity<UserResponse> getRegistrations(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return new ResponseEntity<>(registrationService.getAllRegistration(pageNo, pageSize), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> createRegistration(@RequestBody UserDto userDto) {
        try {
            registrationService.createRegistration(userDto);
            return new ResponseEntity<>("User registered!", HttpStatus.CREATED);
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }



}

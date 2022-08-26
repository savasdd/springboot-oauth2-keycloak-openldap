package com.ldap.controller;

import com.ldap.dto.UserDto;
import com.ldap.services.AuthService;
import com.ldap.utils.kyce.KeycloakTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class AuthController {
    @Autowired
    private AuthService service;

    @PostMapping("/auth")
    public ResponseEntity<KeycloakTokenResponse> getToken(@RequestBody UserDto dto) {
        return new ResponseEntity<>(service.getAuthService().getToken(dto), HttpStatus.OK);
    }


}

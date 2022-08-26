package com.ldap.services.impl;

import com.ldap.dto.UserDto;
import com.ldap.services.async.IAuthService;
import com.ldap.utils.AuthUtils;
import com.ldap.utils.kyce.KeycloakAuthClient;
import com.ldap.utils.kyce.KeycloakAuthResponse;
import com.ldap.utils.kyce.KeycloakTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private AuthUtils util;
    @Autowired
    private KeycloakAuthClient client;

    public KeycloakTokenResponse getToken(UserDto dto) {
        try {
            KeycloakAuthResponse response = client.authenticate(dto);
            log.info("Generate Token: " + dto.getUsername());
            if (util.validateRol(response.getAccessToken()))
                return new KeycloakTokenResponse(response.getAccessToken(), response.getExpiresIn(), response.getTokenType());
            else
                return null;
        } catch (Exception e) {
            log.error("Token Hatası");
            e.printStackTrace();
            return null;
        }
    }
}

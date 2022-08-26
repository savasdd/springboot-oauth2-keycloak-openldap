package com.ldap.services.async;

import com.ldap.dto.UserDto;
import com.ldap.utils.kyce.KeycloakTokenResponse;

public interface IAuthService {

    public KeycloakTokenResponse getToken(UserDto dto);
}

package com.ldap.utils.kyce;

import com.ldap.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KeycloakAuthClient {
    @Value("${keycloak.resource}")
    String clientId;
    @Value("${keycloak.credentials.secret}")
    String clientSecret;

    @Value("${keycloak.auth-server-url}")
    String authUrl;
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    public static final String CLIENT_CREDENTIALS = "password";
    public static final String CLIENT_USERNAME = "username";
    public static final String CLIENT_PASSWORD = "password";


    public KeycloakAuthResponse authenticate(UserDto dto) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add(CLIENT_ID, clientId);
        paramMap.add(CLIENT_SECRET, clientSecret);
        paramMap.add(CLIENT_USERNAME, dto.getUsername());
        paramMap.add(CLIENT_PASSWORD, dto.getPassword());
        paramMap.add(GRANT_TYPE, CLIENT_CREDENTIALS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(paramMap, headers);

        log.info("Try to authenticate");
        final RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KeycloakAuthResponse> response = restTemplate.exchange(authUrl, HttpMethod.POST, entity, KeycloakAuthResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to authenticate");
            throw new RuntimeException("Failed to authenticate");
        }

        log.info("Authentication success");
        return response.getBody();
    }
}

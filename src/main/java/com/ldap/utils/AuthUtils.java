package com.ldap.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ldap.dto.UserDto;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;

@Component
@ToString
public class AuthUtils {


    @Value("${keycloak.resource}")
    private String clientId;

    public UserDto getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Principal principal = (Principal) auth.getPrincipal();
        return new UserDto(auth.getName(), null);
    }

    public Boolean validateRol(String value) throws Exception {
        List<SimpleGrantedAuthority> list = getAutRoles(value);
        if (list.size() > 0)
            for (SimpleGrantedAuthority s : list) {
                if (s.getAuthority().trim().equals(ApiUtil.API_ROL)) {
                    return true;
                }
            }
        return false;
    }

    public List<SimpleGrantedAuthority> getAutRoles(String value) throws Exception {
        DecodedJWT decodedJWT = decodeToken(value);
        JsonObject payloadAsJson = decodeTokenPayloadToJsonObject(decodedJWT);//User info
        JsonObject resourceAccess = payloadAsJson.getAsJsonObject("resource_access");
        if (resourceAccess != null) {
            JsonObject clientAccess = resourceAccess.getAsJsonObject(clientId);
            if (clientAccess != null)
                return StreamSupport.stream(clientAccess.getAsJsonArray("roles").spliterator(), false)
                        .map(JsonElement::getAsString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private DecodedJWT decodeToken(String value) throws Exception {
        if (isNull(value)) {
            throw new Exception("Token has not been provided");
        }
        return JWT.decode(value);
    }

    private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) throws Exception {
        try {
            String payloadAsString = decodedJWT.getPayload();
            return new Gson().fromJson(
                    new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
                    JsonObject.class);
        } catch (RuntimeException exception) {
            throw new Exception("Invalid JWT or JSON format of each of the jwt parts", exception);
        }
    }

    public Date dateConvertToString(String date) {
        Date value = null;
        try {
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            value = format.parse(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}

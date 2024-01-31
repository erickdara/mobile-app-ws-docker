package com.developer.app.ws.security;

import com.developer.app.ws.SpringApplicationContext;
import com.developer.app.ws.service.UserService;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * Attempts to authenticate a user based on the provided HTTP request.
     * This method overrides the standard Spring Security authentication method 
     * to process the user's login credentials from the request.
     *
     * @param req The HTTP request containing the user's login credentials.
     * @param res The HTTP response. In this method implementation, it is not directly used.
     * @return An {@link Authentication} object representing the authenticated user.
     * @throws AuthenticationException If authentication fails due to invalid credentials or other authentication issues.
     * @throws RuntimeException If there is an I/O error while processing the request (e.g., malformed JSON in request body).
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            // Parse the user's login credentials from the request body.
            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);

            // Create an authentication token with the parsed credentials.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            // Handle I/O errors by throwing a runtime exception.
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles successful authentication by generating a JWT (JSON Web Token) for the authenticated user.
     * This method is called by the Spring Security framework when a user has been successfully authenticated.
     * It creates a JWT token and sets it in the response header along with the user's unique identifier.
     *
     * The JWT is signed using the HS512 algorithm and a secret key derived from the application's security constants.
     * It contains the username as the subject and includes both issued-at and expiration timestamps.
     *
     * Additionally, this method retrieves user details from the application's user service and adds the user's
     * unique ID to the response header.
     *
     * @param req   The HttpServletRequest object that contains the request the client made to the server.
     * @param res   The HttpServletResponse object that contains the response the server sends to the client.
     * @param chain The FilterChain that allows the request to proceed to the next filter in the chain.
     * @param auth  The Authentication object that contains details about the successfully authenticated user.
     * @throws IOException      If an input or output exception occurs during the process.
     * @throws ServletException If a servlet-specific error occurs during the process.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
        Instant now = Instant.now();

        String userName = ((User) auth.getPrincipal()).getUsername();
        String token = Jwts.builder().subject(userName).expiration(
                Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME))).issuedAt(Date.from(now)).signWith(secretKey, SignatureAlgorithm.HS512).compact();

        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);

        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserId", userDto.getUserId());
    }

}


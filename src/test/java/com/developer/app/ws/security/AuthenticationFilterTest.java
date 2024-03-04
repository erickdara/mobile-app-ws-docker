package com.developer.app.ws.security;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void attemptAuthentication() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String jsonCredentials = "{\"email\":\"test@example.com\",\"password\":\"password\"}";
        ServletInputStream servletInputStream = new MockServletInputStream(jsonCredentials.getBytes(StandardCharsets.UTF_8));

        when(request.getInputStream()).thenReturn(servletInputStream);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        Authentication result = authenticationFilter.attemptAuthentication(request, response);

        assertNotNull(result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void attemptAuthenticationException() {
        when(authenticationManager.authenticate(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> {
                    authenticationManager.authenticate(any());
                });
    }

}
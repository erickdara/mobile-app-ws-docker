package com.developer.app.ws.security;

import com.developer.app.ws.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
class AuthenticationFilterTest {

    @InjectMocks
    private AuthenticationFilter authenticationFilter; // Assuming the filter class contains the method
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private Authentication authentication;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityConstants securityConstants;

    @Mock
    private Environment environment;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

//       when(environment.getProperty("tokenSecret")).thenReturn("test_token_secret"); // Mock the token secret for testing
//       when(SpringApplicationContext.getBean("environment")).thenReturn(environment);

    }

    @Test
    void attemptAuthentication() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create JSON credentials and wrap them in a MockServletInputStream
        String jsonCredentials = "{\"email\":\"test@example.com\",\"password\":\"password\"}";
        ServletInputStream servletInputStream = new MockServletInputStream(jsonCredentials.getBytes(StandardCharsets.UTF_8));

        // Stub the HttpServletRequest to return the mock ServletInputStream
        when(request.getInputStream()).thenReturn(servletInputStream);

        // Mock AuthenticationManager to return a valid Authentication object
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Execute the method under test
        Authentication result = authenticationFilter.attemptAuthentication(request, response);

        // Verify and assert the expected behavior
        assertNotNull(result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void successfulAuthentication() throws ServletException, IOException {

/*        // Setup
        String userName = "testUser";
        User principal = new User(userName, "password", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(principal);

        UserDto userDto = new UserDto();
        userDto.setUserId("userId123");
        when(userService.getUser(userName)).thenReturn(userDto);

//        when(SecurityConstants.getTokenSecret()).thenReturn(getTokenSecret());

        // Act
        authenticationFilter.successfulAuthentication(request, response, chain, authentication);

        // Assert
        verify(response).addHeader(eq(SecurityConstants.HEADER_STRING), startsWith(SecurityConstants.TOKEN_PREFIX));
        verify(response).addHeader("UserId", userDto.getUserId());*/
    }
}
package com.developer.app.ws.exceptions;

import com.developer.app.ws.ui.model.response.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppExceptionsHandlerTest {

    @InjectMocks
    private AppExceptionsHandler handler;

    private ErrorMessage errorMessage;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void testHandleUserServiceException() {
        errorMessage = new ErrorMessage(new Date(), "Test error message", HttpStatus.CONFLICT.toString(), "Test message");

        UserServiceException mockException = mock(UserServiceException.class);
        when(mockException.getMessage()).thenReturn("Test error message");


        ResponseEntity<Object> response = handler.handleUserServiceException(mockException);

        assertThat(errorMessage.getTimestamp()).isCloseTo(new Date(), 1000);
        assertEquals(errorMessage.getError(), "Test error message");
        assertEquals(errorMessage.getStatus(), HttpStatus.CONFLICT.toString());
        assertEquals(errorMessage.getMessage(), "Test message");
    }


    @Test
    void handleOtherException() {
        errorMessage = new ErrorMessage(new Date(), "Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Test Message");

        Exception mockException = mock(Exception.class);
        when(mockException.getMessage()).thenReturn("Test error message");


        ResponseEntity<Object> response = handler.handleOtherException(mockException);

        assertThat(errorMessage.getTimestamp()).isCloseTo(new Date(), 1000);
        assertEquals(errorMessage.getError(), "Something went wrong.");
        assertEquals(errorMessage.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
        assertEquals(errorMessage.getMessage(), "Test Message");
    }

    @Test
    void handleUserNotFoundException() {

        errorMessage = new ErrorMessage(new Date(), "User Id not found", HttpStatus.NOT_FOUND.toString(), "Test Message");

        UsernameNotFoundException mockException = mock(UsernameNotFoundException.class);
        when(mockException.getMessage()).thenReturn("Test error message");


        ResponseEntity<Object> response = handler.handleUserNotFoundException(mockException);

        assertThat(errorMessage.getTimestamp()).isCloseTo(new Date(), 1000);
        assertEquals(errorMessage.getError(), "User Id not found");
        assertEquals(errorMessage.getStatus(), HttpStatus.NOT_FOUND.toString());
        assertEquals(errorMessage.getMessage(), "Test Message");
    }
}
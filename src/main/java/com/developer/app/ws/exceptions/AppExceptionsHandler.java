package com.developer.app.ws.exceptions;

import com.developer.app.ws.ui.model.response.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class AppExceptionsHandler {

    @ExceptionHandler(value = UserServiceException.class)
    public ResponseEntity<Object> handleUserServiceException(UserServiceException e) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), "CONFLICT", HttpStatus.CONFLICT.toString(), e.getMessage());

        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleOtherException(Exception e) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), "Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UsernameNotFoundException e) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), "User Id not found", HttpStatus.NOT_FOUND.toString(), e.getMessage());

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
}

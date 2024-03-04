package com.developer.app.ws.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {
    private Date timestamp;
    private String error;
    private String status;
    private String message;

}

package com.developer.app.ws.ui.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailsRequestModel {

    @NotNull
    @NotBlank
    @NotEmpty
    private String firstName;
    @NotNull
    @NotBlank
    @NotEmpty
    private String lastName;
    @Email
    @NotBlank
    @NotEmpty
    private String email;
    @NotNull
    @NotBlank
    @NotEmpty
    private String password;
    @NotBlank
    @NotEmpty
    private List<AddressRequestModel> addresses;
}

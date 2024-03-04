package com.developer.app.ws.ui.controller;

import com.developer.app.ws.service.AddressesService;
import com.developer.app.ws.service.UserService;
import com.developer.app.ws.shared.dto.AddressDTO;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.request.UserDetailsRequestModel;
import com.developer.app.ws.ui.model.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Tag(name = "User", description = "Rest API that manage the user personal information registration for app mobile")
@RestController
@RequestMapping("users")  //http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressesService addressesService;

    @Operation(summary = "Get user information by id", description = "Get detailed information about the specified user id", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = UserRest.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Invalid input", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {

        ModelMapper modelMapper = new ModelMapper();
        UserRest returnValue;

        UserDto userDto = userService.getUserByUserId(id);
        returnValue = modelMapper.map(userDto, UserRest.class);

        return returnValue;
    }

    @Operation(summary = "Create User", description = "Allows the creation of new user", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = UserRest.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue;

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @Operation(summary = "Update user information registered", description = "Allow the updating of the information of specified user by ID", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = UserRest.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = new UserDto();
        modelMapper.map(userDetails, userDto);

        UserDto createdUser = userService.updateUser(id, userDto);
        modelMapper.map(createdUser, returnValue);

        return returnValue;
    }

    @Operation(summary = "Delete a user by id", description = "Delete a user specified by ID", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid user value", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(
            @Parameter(description = "User id to delete", required = true) @PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @Operation(summary = "Get all list users", description = "Get all Users", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRest.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Invalid input", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2") int limit) {

        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @Operation(summary = "Get a list of addresses by user", description = "Get detailed list of addresses specified an ID of the user", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = UserRest.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Invalid input", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<AddressesRest> getUserAddresses(@PathVariable String id) {

        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDTO> addressesDto = addressesService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            returnValue = new ModelMapper().map(addressesDto, listType);
        }

        return returnValue;
    }

    @Operation(summary = "Get user address by id", description = "Get detailed address data specified user ID and address ID", security = {
            @SecurityRequirement(name = "Bearer Authentication")}, method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = UserRest.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Invalid input", content = {@Content(mediaType = "application/xml", schema = @Schema(implementation = ErrorMessage.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDTO addressDto = addressesService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDto, AddressesRest.class);

        // http://localhost:8080/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        // http://localhost:8080/users/<userId>/addresses
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
//                .slash(userId)
//                .slash("addresses")
                .withRel("addresses");
        // http://localhost:8080/users/<userId>/{addressId}
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserAddress(userId, addressId))
                .withSelfRel();

//                .slash(userId)
//                .slash("addresses")
//                .slash(addressId)
//
//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }
}

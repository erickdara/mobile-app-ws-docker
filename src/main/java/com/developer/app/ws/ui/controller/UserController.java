package com.developer.app.ws.ui.controller;

import com.developer.app.ws.service.AddressesService;
import com.developer.app.ws.service.UserService;
import com.developer.app.ws.shared.dto.AddressDTO;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.request.UserDetailsRequestModel;
import com.developer.app.ws.ui.model.response.AddressesRest;
import com.developer.app.ws.ui.model.response.OperationStatusModel;
import com.developer.app.ws.ui.model.response.RequestOperationStatus;
import com.developer.app.ws.ui.model.response.UserRest;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("users")  //http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressesService addressesService;

    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id) {

        ModelMapper modelMapper = new ModelMapper();
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        returnValue = modelMapper.map(userDto, UserRest.class);

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

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

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @Operation(summary = "Get List User")
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

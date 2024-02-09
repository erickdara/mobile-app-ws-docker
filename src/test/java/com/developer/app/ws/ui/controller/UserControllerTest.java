package com.developer.app.ws.ui.controller;

import com.developer.app.ws.service.AddressesService;
import com.developer.app.ws.service.UserService;
import com.developer.app.ws.shared.dto.AddressDTO;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.request.UserDetailsRequestModel;
import com.developer.app.ws.ui.model.response.AddressesRest;
import com.developer.app.ws.ui.model.response.OperationStatusModel;
import com.developer.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    final String USER_ID = "asdffd787sdf6556sdf";
    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;

//    @InjectMocks
//    private UserAddressesController controller;
    UserDto userDto;
    @Mock
    private AddressesService addressesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();

        userDto.setFirstName("Erick");
        userDto.setLastName("Rangel");
        userDto.setEmail("test@test.com");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses(getAddressDto());
        userDto.setEncryptedPassword("xcf58tugh47");
    }

    @Test
    void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
        assertEquals(USER_ID, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());

    }

    @Test
    void createUser() throws Exception {

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Erick");
        userDetailsRequestModel.setLastName("Rangel");
        userDetailsRequestModel.setEmail("test@test.com");

        UserRest userRest = userController.createUser(userDetailsRequestModel);

        assertNotNull(userRest);
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());

    }

    @Test
    void updateUser() {

        when(userService.updateUser(anyString(), any(UserDto.class))).thenReturn(userDto);

        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("New Name");
        userDetailsRequestModel.setLastName("New Last Name");
        userDetailsRequestModel.setEmail("test@test.com");

        UserRest userRest = userController.updateUser(USER_ID, userDetailsRequestModel);

        assertNotNull(userRest);
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
    }

    @Test
    void deleteUser() {

        String userId = "someUserId";
        OperationStatusModel operation = new OperationStatusModel();
        operation.setOperationName(RequestOperationName.DELETE.name());
        lenient().when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
        assertEquals("DELETE", operation.getOperationName());
    }

    @Test
    void getUsers() {
        int page = 0;
        int limit = 2;
        List<UserDto> expectedUsers = new ArrayList<>();
        expectedUsers.add(new UserDto());
        expectedUsers.add(new UserDto());
        when(userService.getUsers(page, limit)).thenReturn(expectedUsers);

        List<UserRest> actualUsers = userController.getUsers(page, limit);
//        actualUsers.add(new UserRest());

        assertThat(actualUsers).hasSize(2);

    }

    @Test
    void getUserAddresses() {
        // Arrange
        String userId = "validUserId";
        List<AddressDTO> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(new AddressDTO());
        expectedAddresses.add(new AddressDTO());
        when(addressesService.getAddresses(userId)).thenReturn(expectedAddresses);

        // Act
        List<AddressesRest> actualAddresses = userController.getUserAddresses(userId);

        // Assert
        assertThat(actualAddresses).hasSize(2); // Assuming 2 expected addresses
        // ... further assertions to verify content, order, etc.
    }

    @Test
    void getUserAddress() {
        // Arrange
        String userId = "validUserId";
        String addressId = "validAddressId";
        AddressDTO expectedAddress = new AddressDTO();
        when(addressesService.getAddress(addressId)).thenReturn(expectedAddress);

        // Act
        EntityModel<AddressesRest> actualResponse = userController.getUserAddress(userId, addressId);

        // Assert
        AddressesRest returnValue = actualResponse.getContent();
        // ... assertions for addressesRest content
        List<Link> links = actualResponse.getLinks().toList();
        // ... assertions for links (user, userAddresses, self)

        // Assert
        //assertThat(actualResponse.getContent()).isEqualTo(expectedAddress);
        assertThat(actualResponse.getLinks()).hasSize(3);
        // ... further assertions to verify link content and REL values
    }

    private List<AddressDTO> getAddressDto() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setType("shipping");
        addressDTO.setCity("Bogota");
        addressDTO.setCountry("Colombia");
        addressDTO.setPostalCode("111041");
        addressDTO.setStreetName("123 Street Name");

        AddressDTO billingAddressDTO = new AddressDTO();
        billingAddressDTO.setType("billing");
        billingAddressDTO.setCity("Bogota");
        billingAddressDTO.setCountry("Colombia");
        billingAddressDTO.setPostalCode("111041");
        billingAddressDTO.setStreetName("789 Street Name");

        List<AddressDTO> addresses = new ArrayList<>();
        addresses.add(addressDTO);
        addresses.add(billingAddressDTO);
        return addresses;
    }
}
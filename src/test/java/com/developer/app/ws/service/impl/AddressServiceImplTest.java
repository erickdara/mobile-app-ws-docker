package com.developer.app.ws.service.impl;

import com.developer.app.ws.io.entity.AddressEntity;
import com.developer.app.ws.io.entity.UserEntity;
import com.developer.app.ws.repository.AddressRepository;
import com.developer.app.ws.repository.UserRepository;
import com.developer.app.ws.shared.dto.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    String userId = "yKjEJo8puvoaw6Eobue1EUlitL5v5p";
    String encryptedPassword = "3eer45erwd4";
    UserEntity userEntity;
    @InjectMocks
    private AddressServiceImpl addressService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Erick");
        userEntity.setLastName("Rangel");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    void getAddresses() {
        // Arrange
        String userId = "validUserId";
        List<AddressEntity> addressEntities = new ArrayList<>();
        addressEntities.add(new AddressEntity());
        addressEntities.add(new AddressEntity());

        when(userRepository.findByUserId(userId)).thenReturn(userEntity);
        when(addressRepository.findAllByUserDetails(userEntity)).thenReturn(addressEntities);

        // Act
        List<AddressDTO> addresses = addressService.getAddresses(userId);

        // Assert
        assertThat(addresses).hasSize(2); // Assuming 2 address entities
        // ... further assertions to verify content
    }

    @Test
    void getAddress() {
        // Arrange
        String addressId = "validAddressId";
        AddressEntity expectedEntity = new AddressEntity();
        when(addressRepository.findByAddressId(addressId)).thenReturn(expectedEntity);

        // Act
        AddressDTO actualAddress = addressService.getAddress(addressId);

        // Assert
        assertNotNull(actualAddress);
        // ... assertions to verify mapped properties
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

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDTO> addresses = getAddressDto();

        Type listType = new TypeToken<List<AddressEntity>>() {
        }.getType();
        return new ModelMapper().map(addresses, listType);
    }
}
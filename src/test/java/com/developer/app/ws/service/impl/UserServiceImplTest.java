package com.developer.app.ws.service.impl;

import com.developer.app.ws.exceptions.UserServiceException;
import com.developer.app.ws.io.entity.AddressEntity;
import com.developer.app.ws.io.entity.UserEntity;
import com.developer.app.ws.repository.UserRepository;
import com.developer.app.ws.shared.Utils;
import com.developer.app.ws.shared.dto.AddressDTO;
import com.developer.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "yKjEJo8puvoaw6Eobue1EUlitL5v5p";
    String encryptedPassword = "3eer45erwd4";

    UserEntity userEntity;

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
    void getUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);
        assertEquals("Erick", userDto.getFirstName());
    }

    @Test
    final void getUser_UsernameNotFoundException(){
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser("test@test.com");
                });
    }

    @Test
    final void createUser_ServiceException(){
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressDto());
        userDto.setFirstName("Erick");
        userDto.setLastName("Rangel");
        userDto.setPassword("123456789");
        userDto.setEmail("test@test.com");

        assertThrows(UserServiceException.class,
                () -> {
                    userService.createUser(userDto);
                });
    }


    @Test
    void createUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(utils.generateAddressId(anyInt())).thenReturn("MP2M8eXL1sdAGagmyanBO0A04CRnDk");
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressDto());
        userDto.setFirstName("Erick");
        userDto.setLastName("Rangel");
        userDto.setPassword("123456789");
        userDto.setEmail("test@test.com");

        UserDto storedUserDetails = userService.createUser(userDto);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(2)).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("123456789");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    private List<AddressDTO> getAddressDto(){
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

    private List<AddressEntity> getAddressesEntity()
    {
        List<AddressDTO> addresses = getAddressDto();

        Type listType = new TypeToken<List<AddressEntity>>(){}.getType();
        return new ModelMapper().map(addresses, listType);
    }


}
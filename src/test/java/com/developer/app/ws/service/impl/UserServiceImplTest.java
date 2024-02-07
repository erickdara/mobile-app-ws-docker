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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
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
    void loadUserByName(){
        String email ="erick@test.com";
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals(encryptedPassword, userDetails.getPassword());

    }

    @Test
    void loadUserByName_NotFoundException(){
        String email ="nonexistent@test.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Execute & Verify
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
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
    void getUserByUserId(){
        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUserByUserId(userId);

        assertEquals(userId,userDto.getUserId());

    }

    @Test
    void getUserByUserId_UsernameNotFoundException(){
        when(userRepository.findByUserId(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser("test@test.com");
                });
    }

    @Test
    void updateUser(){
        //Setup
        String userId = "uniqueUserId";
        UserDto userToUpdate = new UserDto();
        userToUpdate.setFirstName("New FirstName");
        userToUpdate.setLastName("New LastName");

        UserEntity storedUserEntity = new UserEntity();
        storedUserEntity.setUserId(userId);
        storedUserEntity.setFirstName("Old FirstName");
        storedUserEntity.setLastName("Old LastName");

        when(userRepository.findByUserId(anyString())).thenReturn(storedUserEntity);
        when(userRepository.save(any(UserEntity.class))).thenAnswer( i -> i.getArguments()[0] );

        UserDto updatedUser = userService.updateUser(userId, userToUpdate);

        assertEquals(userToUpdate.getFirstName(), updatedUser.getFirstName());
        assertEquals(userToUpdate.getLastName(), updatedUser.getLastName());
        verify(userRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));

    }

    @Test
    void updateUser_UserNotFound(){
        String userId = "nonExistentUserId";
        UserDto user = new UserDto();
        when(userRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(UserServiceException.class, ()-> userService.updateUser(userId, user));
    }

    @Test
    void deleteUser(){
        String userId = "someUserId";

        when(userRepository.findByUserId(userId)).thenReturn(userEntity);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(userEntity);


    }

    @Test
    void deleteUser_UserServiceException(){
        String userId = "nonExistentUserId";
        UserDto user = new UserDto();
        when(userRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(UserServiceException.class, ()-> userService.deleteUser(userId));
    }

    @Test
    void getUsers(){

        int page=1;
        int limit=2;

        List<UserEntity> userList = new ArrayList<>();
        userList.add(new UserEntity());
        userList.add(new UserEntity());
        Page<UserEntity> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(PageRequest.of(0, limit))).thenReturn(userPage);

        // Execute
        List<UserDto> result = userService.getUsers(page, limit);

        // Verify
        assertEquals(limit, result.size());
        verify(userRepository, times(1)).findAll(any(PageRequest.class));
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
package com.developer.app.ws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() {
    }

    @Test
    void generateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId);
        assertNotNull(userId2);

        assertEquals(30, userId.length());
        assertFalse(userId.equalsIgnoreCase(userId2));

    }

    @Test
    void generateAddressId() {

        String addressId = utils.generateAddressId(30);
        String addressId2 = utils.generateAddressId(30);

        assertNotNull(addressId);
        assertNotNull(addressId2);

        assertEquals(30, addressId.length());
        assertFalse(addressId.equalsIgnoreCase(addressId2));
    }
}
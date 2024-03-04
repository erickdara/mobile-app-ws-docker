package com.developer.app.ws.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SecurityConstantsTest {

    @Test
    public void testConstants() {
        assertEquals(864000000, SecurityConstants.EXPIRATION_TIME);
        assertEquals("Bearer ", SecurityConstants.TOKEN_PREFIX);
        assertEquals("Authorization", SecurityConstants.HEADER_STRING);
        assertEquals("/users", SecurityConstants.SIGN_UP_URL);
    }

    @Test
    public void testGetTokenSecret_Success() {
        String expectedTokenSecret = "ABChZzU4NDg5YW1aTDCBCB4waDc6TUp3YWN4RU5WNzQ1bEdQNWJPdlFETV9iaDE5NGp1eHQ3SXJfdWEzQQ==";
        //lenient().when(environment.getProperty("tokenSecret")).thenReturn(expectedTokenSecret);

        String actualTokenSecret = SecurityConstants.getTokenSecret();

        assertEquals(expectedTokenSecret, actualTokenSecret);
    }
}
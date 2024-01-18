package com.developer.app.ws.security;

import com.developer.app.ws.SpringApplicationContext;
import org.springframework.core.env.Environment;


public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864000000; // 10 Days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";

    public static final String TOKEN_SECRET = "ABChZzU4NDg5YW1aTDCBCB4waDc6TUp3YWN4RU5WNzQ1bEdQNWJPdlFETV9iaDE5NGp1eHQ3SXJfdWEzQQ==";

    public static String getTokenSecret() {
        Environment environment = (Environment) SpringApplicationContext.getBean("environment");
        return environment.getProperty("tokenSecret");
    }
}

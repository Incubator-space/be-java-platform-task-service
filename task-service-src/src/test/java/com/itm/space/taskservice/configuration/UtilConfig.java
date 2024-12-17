package com.itm.space.taskservice.configuration;

import com.itm.space.itmplatformcommonmodels.util.AuthUtil;
import com.itm.space.taskservice.initializer.KeycloakInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UtilConfig {

    private static final String REALM = "ITM-Platform";

    private static final String CLIENT_ID = "gateway";

    private static final String CLIENT_SECRET = "jpnYK5CvskQbjY2B9W7j8rfU5oLiaU5R";

    private static final String DEFAULT_PASSWORD = "passwd";

    @Bean
    public AuthUtil authUtil() {

        String keycloakUrl = KeycloakInitializer.container.getAuthServerUrl();
        return new AuthUtil(keycloakUrl, REALM, CLIENT_ID, CLIENT_SECRET, DEFAULT_PASSWORD);
    }
}

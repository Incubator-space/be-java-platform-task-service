package com.itm.space.taskservice;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.api.DBRider;
import com.itm.space.itmplatformcommonmodels.util.AuthUtil;
import com.itm.space.itmplatformcommonmodels.util.JsonParserUtil;
import com.itm.space.taskservice.configuration.UtilConfig;
import com.itm.space.taskservice.initializer.KafkaInitializer;
import com.itm.space.taskservice.initializer.KeycloakInitializer;
import com.itm.space.taskservice.initializer.PostgresInitializer;
import com.itm.space.taskservice.initializer.WiremockInitializer;
import com.itm.space.taskservice.service.TestConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(initializers = {
        PostgresInitializer.class, KafkaInitializer.class, KeycloakInitializer.class, WiremockInitializer.class
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(UtilConfig.class)
@DBRider
@DBUnit(caseSensitiveTableNames = true, schema = "public")
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestConsumerService testConsumerService;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected AuthUtil authUtil;

    protected JsonParserUtil jsonParserUtil = new JsonParserUtil();
}
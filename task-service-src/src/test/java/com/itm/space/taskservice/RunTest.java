package com.itm.space.taskservice;

import org.junit.jupiter.api.Test;

import static com.itm.space.taskservice.initializer.KafkaInitializer.kafkaContainer;
import static com.itm.space.taskservice.initializer.PostgresInitializer.postgreSQLContainer;

class RunTest extends BaseIntegrationTest {

    @Test
    void postgreSQLContainerIsRunning() {
        postgreSQLContainer.isRunning();
    }

    @Test
    void kafkaContainerIsRunning() {
        kafkaContainer.isRunning();
    }
}
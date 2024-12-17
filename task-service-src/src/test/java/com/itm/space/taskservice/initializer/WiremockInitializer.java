package com.itm.space.taskservice.initializer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

public class WiremockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static WireMockServer wireMockServer;
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());

        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

        System.setProperty("adapter.chatGPT.connectionUrl", "http://localhost:" + wireMockServer.port());
    }
}
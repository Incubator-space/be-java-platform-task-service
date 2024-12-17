package com.itm.space.taskservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;

    private static final String OAUTH_SCHEME_NAME = "oauth2_password";


    @Bean
    public OpenAPI apiInfo() {
        Info apiInfo = new Info()
                .title("Task Service REST APIs")
                .version("1.0.0");
        Components components = new Components().addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme());
        final var localServer = new Server().description("Локальное окружение").url("http://localhost:9999");
        final var devServer = new Server().description("DEV").url("https://api.migration.it-mentor.space/task-service");
        return new OpenAPI().info(apiInfo)
                .servers(List.of(devServer, localServer))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows();
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = createFlow();
        return new OAuthFlows().password(flow);
    }

    private OAuthFlow createFlow() {
        var flow = new OAuthFlow();
        flow.setTokenUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token");
        return flow;
    }
}

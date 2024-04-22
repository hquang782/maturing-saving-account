package org.studytest.saving_account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${test.openapi.dev-url}")
    private String devUrl;

    @Value("${test.openapi.dev-title}")
    private String devTitle;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");


        Info info = new Info()
                .title(devTitle)
                .version("1.0")
                .description("This API exposes endpoints to manage tutorials.");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
package br.com.iotasoftware.gadoapp.gadoappapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gadoapp API")
                        .version("v1")
                        .description("API para gerenciamento de gado bovino")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
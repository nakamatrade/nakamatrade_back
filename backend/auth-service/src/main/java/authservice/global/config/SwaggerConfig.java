package authservice.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_TITLE = "Nakamatrade API";
    private static final String API_DESCRIPTION = "providing authentication, user management";
    private static final String API_VERSION = "0.0.1";

    private static final String SECURITY_SCHEME_ACCESS_TOKEN = "Access Token";
    private static final String SECURITY_SCHEME_TYPE = "Bearer";
    private static final String SECURITY_SCHEME_BEARER_FORMAT = "JWT";

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(createInfo())
                .components(components());
    }

    private Info createInfo() {
        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(API_VERSION);
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(SECURITY_SCHEME_TYPE)
                .bearerFormat(SECURITY_SCHEME_BEARER_FORMAT)
                .in(SecurityScheme.In.HEADER);
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_ACCESS_TOKEN, securityScheme());
    }
}

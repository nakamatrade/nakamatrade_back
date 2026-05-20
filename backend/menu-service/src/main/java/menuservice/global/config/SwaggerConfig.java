package menuservice.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_TITLE = "Nakamatrade API";
    private static final String API_DESCRIPTION = "menu management";
    private static final String API_VERSION = "0.0.1";

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(createInfo());
    }

    private Info createInfo() {
        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(API_VERSION);
    }
}

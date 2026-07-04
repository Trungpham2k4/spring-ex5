package fa.training.ex5.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI getOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Áp dụng cơ chế bảo mật cho tất cả các API hiển thị trên Swagger
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Định nghĩa cơ chế xác thực JWT Bearer Token
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
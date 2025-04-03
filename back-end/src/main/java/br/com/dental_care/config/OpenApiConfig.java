package br.com.dental_care.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dental Appointment Scheduling System")
                        .version("1.0")
                        .description("API for scheduling dental appointments, including features for booking, cancellation, and checking available time slots")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Hideyuki Takahashi")
                                .url("https://github.com/Dev-HideyukiTakahashi/dental-care")
                                .email("dev.hideyukitakahashi@gmail.com"))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))

                )
                .components(new Components()
                        .addSecuritySchemes("OAuth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl("/oauth2/token")
                                        )
                                )
                        )
                )
                .security(List.of(new SecurityRequirement().addList("OAuth2")));
    }
}

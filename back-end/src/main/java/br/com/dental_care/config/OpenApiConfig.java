package br.com.dental_care.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

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
                );
    }
}

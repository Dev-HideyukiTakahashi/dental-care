package br.com.dental_care;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableScheduling
public class DentalCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentalCareApplication.class, args);
	}

}

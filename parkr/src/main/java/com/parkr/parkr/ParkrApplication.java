package com.parkr.parkr;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.parkr.parkr.common.GoogleServices;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@SpringBootApplication
@SecurityScheme(name = "parkr", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER, bearerFormat = "JWT")
public class ParkrApplication {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
		
	}
	public static void main(String[] args) {
		//System.out.println(GoogleServices.crawlNearbyLots(39.865398, 32.748820, "en")); // nearby bilkent. 
		//System.out.println(GoogleServices.crawlNearbyLots(39.922983, 32.854057, "en")); // nearby kızılay. 

		SpringApplication.run(ParkrApplication.class, args);
	}

}

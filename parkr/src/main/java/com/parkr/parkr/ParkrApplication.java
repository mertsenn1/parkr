package com.parkr.parkr;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@SpringBootApplication
@SecurityScheme(name = "parkr", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class ParkrApplication {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
		
	}
	public static void main(String[] args) {
		//System.out.println(GoogleServices.crawlNearbyLots(39.865398, 32.748820, "en", true)); // nearby bilkent. 

		SpringApplication.run(ParkrApplication.class, args);
	}

}

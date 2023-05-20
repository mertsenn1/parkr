package com.parkr.parkr;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.parkr.parkr.common.GoogleServices;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@SpringBootApplication
@EnableCaching
@SecurityScheme(name = "parkr", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER, bearerFormat = "JWT")
public class ParkrApplication {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
		
	}
	public static void main(String[] args) {
		//System.out.println(GoogleServices.crawlNearbyLots(39.865398, 32.748820, "en")); // nearby bilkent. 
		//System.out.println(GoogleServices.crawlNearbyLots(39.922983, 32.854057, "en")); // nearby kızılay. 

		//System.out.println(GoogleServices.getEcoFriendlyRoute(39.865398, 32.748820, "ChIJzdMwpRBH0xQRxb61KFCWYD4", "GASOLINE"));
		/* 
		List<String> destinations = new ArrayList<>();
		destinations.add("ChIJzdMwpRBH0xQRxb61KFCWYD4");
		destinations.add("ChIJc02rwPRH0xQRVbeWdZeM2mI");
		destinations.add("ChIJT_bteNZA0xQRXtE7iEABEdk");
		destinations.add("ChIJlw--qkdG0xQRDa78VHdA6Gk");
		System.out.println(GoogleServices.getRouteDistances(39.866248, 32.750615, destinations));
		*/
		SpringApplication.run(ParkrApplication.class, args);
	}

}

package com.romaincaron.data_collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DataCollectionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataCollectionServiceApplication.class, args);
	}

}

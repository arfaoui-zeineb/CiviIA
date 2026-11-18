package com.civia.municipalcommunicationeventsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MunicipalCommunicationEventsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MunicipalCommunicationEventsServiceApplication.class, args);
	}

}

package it.smartcommunitylab.trento.mobilitydatawrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatawrapperServicesApplication {

	public static void main(String[] args) {
			SpringApplication.run(DatawrapperServicesApplication.class, args);
	}

}

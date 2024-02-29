package org.snubi.did.issuerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class DidIssuerServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DidIssuerServerApplication.class, args);
	}

}

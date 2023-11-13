package com.bwwd.BestWorldWideDeals;

import com.bwwd.BestWorldWideDeals.Utils.SystemStorageUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
@ComponentScan("com.bwwd")
public class BestWorldWideDealsApplication implements CommandLineRunner {

	public static void main(String[] args) {

		SpringApplication.run(BestWorldWideDealsApplication.class, args);

	}

	public void run(String... arg) throws Exception {
		SystemStorageUtil.deleteAll();
		SystemStorageUtil.init();
	}
}

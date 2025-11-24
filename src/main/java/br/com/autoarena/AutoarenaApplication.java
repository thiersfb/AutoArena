package br.com.autoarena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType; // Adicione este import

@SpringBootApplication
public class AutoarenaApplication {

//	public static void main(String[] args) {
//		SpringApplication application = new SpringApplication(AutoarenaApplication.class);
//		application.setWebApplicationType(WebApplicationType.SERVLET); // Adicione esta linha
//		application.run(args);
//	}

	public static void main(String[] args) {
		SpringApplication.run(AutoarenaApplication.class, args);
	}

}

package cn.liguohao.demo.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		FileConstants.initDir();
		SpringApplication.run(Application.class, args);
	}

}

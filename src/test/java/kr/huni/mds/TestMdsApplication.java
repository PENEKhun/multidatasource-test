package kr.huni.mds;

import org.springframework.boot.SpringApplication;

public class TestMdsApplication {

	public static void main(String[] args) {
		SpringApplication.from(MdsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

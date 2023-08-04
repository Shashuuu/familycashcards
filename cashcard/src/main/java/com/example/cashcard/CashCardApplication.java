package com.example.cashcard;

import com.example.cashcard.model.CashCardUser;
import com.example.cashcard.repo.CashCardUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CashCardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashCardApplication.class, args);
	}

//	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//
//	@Bean
//	public CommandLineRunner demo(CashCardUserRepository cashCardUserRepository) {
//		return args -> {
//			cashCardUserRepository.save(
//					new CashCardUser(
//							"shashu",
//							bCryptPasswordEncoder.encode("Shashuu"),
//							"ROLE_OWNER"
//					)
//			);
//		};
//	}

}

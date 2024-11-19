package ferpaks.cinema.configuration;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.CinemaUserRole;
import ferpaks.cinema.repository.CinemaUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class AdminAccountInitializer {
	@Value("${account.admin.username}")
	private String username;

	@Value("${account.admin.password}")
	private String password;

	@Bean
	public CommandLineRunner initAdminUser(CinemaUserRepository userRepository, PasswordEncoder passwordEncoder) {

		return args -> {
			if (!userRepository.existsByRole(CinemaUserRole.ADMIN) && userRepository.findByUsername(username).isEmpty()) {
				CinemaUser admin = new CinemaUser();

				admin.setUsername(username);
				admin.setPassword(passwordEncoder.encode(password));
				admin.setRole(CinemaUserRole.ADMIN);
				admin.setBalanceInRubles(new BigDecimal("10000.0"));

				userRepository.save(admin);

				System.out.println("Тестовый администратор создан.");
			}
		};
	}
}
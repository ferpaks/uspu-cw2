package ferpaks.cinema.service;

import ferpaks.cinema.dto.RegistrationDto;
import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.CinemaUserRole;
import ferpaks.cinema.repository.CinemaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CinemaUserRegistrationService {
	private final CinemaUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public CinemaUser registerNewUser(RegistrationDto registrationDto) {
		if (userRepository.existsByUsername(registrationDto.getUsername())) {
			throw new IllegalArgumentException("Пользователь с таким именем уже существует");
		}

		if (registrationDto.getUsername().length() < 3 || registrationDto.getUsername().length() > 32) {
			throw new IllegalArgumentException("Имя пользователя должно быть от 3 до 32 символов");
		}

		if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
			throw new IllegalArgumentException("Пароли не совпадают");
		}

		String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());

		CinemaUser user = new CinemaUser();
		user.setUsername(registrationDto.getUsername());
		user.setPassword(encodedPassword);
		user.setRole(CinemaUserRole.USER);
		user.setBalanceInRubles(BigDecimal.ZERO);

		return userRepository.save(user);
	}
}
package ferpaks.cinema.initializer;

import ferpaks.cinema.dto.RegistrationDto;
import ferpaks.cinema.entity.CinemaUserRole;
import ferpaks.cinema.service.CinemaUserRegistrationService;
import ferpaks.cinema.service.CinemaUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserInitializer {
	private final CinemaUserService userService;
	private final CinemaUserRegistrationService userRegistrationService;

	/**
	 * Если нет ни одного пользователя с ролью USER, то создаёт несколько таких пользователей.
	 */
	@PostConstruct
	public void initUsers() {
		boolean hasUsersWithRoleUser = userService.existsByUserRole(CinemaUserRole.USER);

		if (!hasUsersWithRoleUser) {
			RegistrationDto panda = new RegistrationDto();
			panda.setUsername("panda");
			panda.setPassword("password");
			panda.setConfirmPassword("password");

			RegistrationDto elephant = new RegistrationDto();
			elephant.setUsername("elephant");
			elephant.setPassword("password");
			elephant.setConfirmPassword("password");

			RegistrationDto gorilla = new RegistrationDto();
			gorilla.setUsername("gorilla");
			gorilla.setPassword("password");
			gorilla.setConfirmPassword("password");

			RegistrationDto shark = new RegistrationDto();
			shark.setUsername("shark");
			shark.setPassword("password");
			shark.setConfirmPassword("password");

			List<RegistrationDto> registrationDtos = List.of(
					panda,
					elephant,
					gorilla,
					shark
			);

			for (RegistrationDto dto : registrationDtos) {
			    userRegistrationService.registerNewUser(dto);
			}

			System.out.println("Инициализация нескольких новых пользователей завершена.");
		}
	}
}
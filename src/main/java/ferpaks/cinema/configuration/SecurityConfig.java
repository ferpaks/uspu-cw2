package ferpaks.cinema.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						// Общедоступные маршруты
						.requestMatchers("/bookings", "/bookings/**", "/cinemas", "/cinemas/{id}", "/movies", "/movies/{id}", "/users", "/users/{username}", "/sessions", "/sessions/{id}", "/sessions/{id}/bookings", "/sessions/{id}/bookings/new").hasAnyRole("Пользователь", "Администратор", "Менеджер")
						.requestMatchers("/bookings", "/bookings/**", "/halls", "/halls/**", "/cinemas", "/cinemas/**", "/users", "/users/{username}", "/sessions", "/sessions/**", "/movies", "/movies/**").hasAnyRole("Администратор", "Менеджер")
						.requestMatchers("/users/{username}/change-role", "/users/{username}/change-balance").hasRole("Администратор")

						.requestMatchers("/", "/index", "/login", "/register", "/css/**", "/functionality").permitAll()

						// Специфические маршруты для администратора

						// Маршруты для Администраторов и Менеджеров

						// Маршруты для Пользователей, Администраторов и Модераторов

						// Общие проверки доступа
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/")
						.failureUrl("/login?error=true")
						.permitAll()
				)
				.logout(LogoutConfigurer::permitAll);

		return http.build();

		/*
		.antMatchers("/movies/add", "/movies/edit/**", "/movies/delete/**", "/movies/archive/**", "/movies/unarchive/**")
        .hasAnyRole("MANAGER", "ADMIN")
		 */
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
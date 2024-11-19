package ferpaks.cinema.service;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.repository.CinemaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CinemaUserDetailsService implements UserDetailsService {
	private final CinemaUserRepository cinemaUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CinemaUser user = cinemaUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

		return new User(user.getUsername(), user.getPassword(), user.getAuthorities());
	}
}
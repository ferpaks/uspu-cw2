package ferpaks.cinema.service;

import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.CinemaUserRole;
import ferpaks.cinema.repository.CinemaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaUserService {
	private final CinemaUserRepository userRepository;

	public List<CinemaUser> findAll() {
		return userRepository.findAll();
	}

	public List<CinemaUser> findAllByRole(CinemaUserRole role) {
		return userRepository.findAllByRole(role);
	}

	public List<CinemaUser> findAllManagers() {
		return userRepository.findAllByRole(CinemaUserRole.MANAGER).stream()
				.sorted(Comparator.comparing(CinemaUser::getUsername))
				.collect(Collectors.toList());
	}

	public Optional<CinemaUser> findById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<CinemaUser> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean existsByUserRole(CinemaUserRole role) {
		return userRepository.existsByRole(role);
	}

	public void changeUserRole(String username, CinemaUserRole newRole) {
		CinemaUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		user.setRole(newRole);

		userRepository.save(user);
	}

	public void changeUserBalance(String username, BigDecimal newBalance) {
		CinemaUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		newBalance = newBalance.abs();

		user.setBalanceInRubles(newBalance);

		userRepository.save(user);
	}

	public boolean isAdmin(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(CinemaUserRole.ADMIN.getGrantedAuthority().getAuthority()));
	}

	public CinemaUser save(CinemaUser user) {
		return userRepository.save(user);
	}
}
package ferpaks.cinema.controller;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.CinemaUserRole;
import ferpaks.cinema.service.CinemaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final CinemaUserService userService;

	@GetMapping
	public String showUsersByRoles(Model model) {
		// FIXME Что будет, если тут указать не все роли, а только часть?
		List<CinemaUserRole> roleOrder = List.of(CinemaUserRole.ADMIN, CinemaUserRole.MANAGER, CinemaUserRole.STAFF, CinemaUserRole.USER);

		Map<CinemaUserRole, List<CinemaUser>> usersByRoles = new LinkedHashMap<>();

		for (CinemaUserRole role : roleOrder) {
			List<CinemaUser> users = userService.findAll().stream()
					.filter((user) -> user.getRole().equals(role))
					.sorted(Comparator.comparing(CinemaUser::getUsername))
					.collect(Collectors.toList());
			usersByRoles.put(role, users);
		}


		model.addAttribute("usersByRoles", usersByRoles);

		return "user/list";
	}

	@GetMapping("/{username}/change-role")
	public String showChangeRoleForm(@PathVariable String username, Model model) {
		CinemaUser user = userService.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Invalid username: " + username));

		model.addAttribute("user", user);
		model.addAttribute("roles", CinemaUserRole.values());
		return "user/change-role";
	}

	@PostMapping("/{username}/change-role")
	public String changeUserRole(@PathVariable String username, @RequestParam CinemaUserRole newRole) {
		userService.changeUserRole(username, newRole);
		return "redirect:/users";
	}

	@GetMapping("/{username}/change-balance")
	public String showChangeBalance(@PathVariable String username, Model model) {
		CinemaUser user = userService.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Invalid username: " + username));

		model.addAttribute("user", user);
		return "user/change-balance";
	}

	@PostMapping("/{username}/change-balance")
	public String changeBalance(@PathVariable String username, @RequestParam BigDecimal newBalance) {
		userService.changeUserBalance(username, newBalance);
		return "redirect:/users";
	}

	@GetMapping("/{username}")
	public String showUserDetail(@PathVariable String username, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = auth.getName();

		if (userService.isAdmin(auth) || currentUsername.equals(username)) {
			CinemaUser user = userService.findByUsername(username).get();

			model.addAttribute("user", user);
			return "user/detail";
		} else {
			model.addAttribute("errorDescription", "У вас нет доступа к этой информации.");
			return "error/error";
		}

	}
}
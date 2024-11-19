package ferpaks.cinema.controller;

import ferpaks.cinema.dto.RegistrationDto;
import ferpaks.cinema.service.CinemaUserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
	private final CinemaUserRegistrationService cinemaUserRegistrationService;

	@GetMapping("/login")
	public String showLoginPage() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("registrationDto", new RegistrationDto());
		return "auth/register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("registrationDto") @Valid RegistrationDto registrationDto,
							   BindingResult result, Model model) {
		// #TODO Добавить вывод ошибок ещё и в Modal?
		if (result.hasErrors()) {
			return "auth/register";
		}

		try {
			cinemaUserRegistrationService.registerNewUser(registrationDto);
		} catch (IllegalArgumentException e) {
			model.addAttribute("registrationError", e.getMessage());
			return "auth/register";
		}

		return "redirect:/login";
	}
}
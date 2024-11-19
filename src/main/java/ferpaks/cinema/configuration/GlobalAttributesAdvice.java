package ferpaks.cinema.configuration;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.service.CinemaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalAttributesAdvice {
	private final CinemaUserService userService;

	@ModelAttribute
	public void addUserBalanceToModel(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			CinemaUser user = userService.findByUsername(username).orElse(null);
			if (user != null) {
				model.addAttribute("userBalance", user.getBalanceInRubles());
				model.addAttribute("user", user);
			}
		}
	}
}

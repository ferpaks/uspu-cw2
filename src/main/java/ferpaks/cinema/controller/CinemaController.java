package ferpaks.cinema.controller;

import ferpaks.cinema.entity.Address;
import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.CinemaStatus;
import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.repository.CinemaRepository;
import ferpaks.cinema.service.AddressService;
import ferpaks.cinema.service.CinemaService;
import ferpaks.cinema.service.CinemaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cinemas")
@RequiredArgsConstructor
public class CinemaController {
	private final CinemaService cinemaService;
	private final AddressService addressService;
	private final CinemaUserService userService;

	@GetMapping("/new")
	public String showAddCinemaForm(Model model) {
		model.addAttribute("cinema", new Cinema());
		model.addAttribute("addresses", addressService.findAvailableAddresses());
		model.addAttribute("managers", userService.findAllManagers());
		model.addAttribute("statuses", CinemaStatus.values());
		return "cinema/add";
	}

	@PostMapping
	public String addCinema(@ModelAttribute Cinema cinema, @RequestParam Long addressId, @RequestParam Long managerId, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("addresses", addressService.findAvailableAddresses());
			model.addAttribute("managers", userService.findAllManagers());
			model.addAttribute("statuses", CinemaStatus.values());
			return "cinema/add";
		}

		Address address = addressService.findById(addressId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid address ID: " + addressId));
		CinemaUser manager = userService.findById(managerId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid manager ID: " + managerId));

		cinema.setAddress(address);
		cinema.setManager(manager);

		if (cinemaService.existsByAddressId(addressId)) {
			// TODO Узнать что за result.rejectValue()
			result.rejectValue("address", "error.cinema", "Кинотеатр с этим адресом уже существует.");
			model.addAttribute("addresses", addressService.findAvailableAddresses());
			model.addAttribute("managers", userService.findAllManagers());
			model.addAttribute("statuses", CinemaStatus.values());
			return "cinema/add";
		}

		try {
			cinemaService.save(cinema);
		} catch (IllegalArgumentException e) {
			//TODO Добавить вывод ошибок на страницу-шаблон
		}
		return "redirect:/cinemas";
	}

	@GetMapping("/{link}/edit")
	public String showEditCinemaForm(@PathVariable String link, Model model) {
		Cinema cinema = cinemaService.findByLinkName(link)
				.orElseThrow(() -> new IllegalArgumentException("Invalid cinema link: " + link));

		List<Address> availableAddresses = addressService.findAvailableAddresses();
		if (cinema.getAddress() != null && !availableAddresses.contains(cinema.getAddress())) {
			availableAddresses.add(cinema.getAddress());
		}

		model.addAttribute("cinema", cinema);
		model.addAttribute("addresses", availableAddresses);
		model.addAttribute("managers", userService.findAllManagers());
		model.addAttribute("statuses", CinemaStatus.values());
		return "cinema/edit";
	}

	@PutMapping("/{link}")
	public String editCinema(@PathVariable String link, @ModelAttribute Cinema cinema, @RequestParam Long addressId, @RequestParam Long managerId, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("addresses", addressService.findAvailableAddresses());
			model.addAttribute("managers", userService.findAllManagers());
			model.addAttribute("statuses", CinemaStatus.values());
			return "cinema/edit";
		}

		cinema.setAddress(addressService.findById(addressId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid address ID")));
		cinema.setManager(userService.findById(managerId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user (manager) ID")));

		cinemaService.update(link, cinema);
		return "redirect:/cinemas";
	}

	@GetMapping
	public String showList(Model model, Authentication authentication) {
		String currentUsername = authentication.getName();

		List<Cinema> cinemas = cinemaService.findAll();
		cinemas.sort(
				Comparator.comparing((Cinema cinema) -> cinema.getAddress().getCity().getName())
						.thenComparing(Cinema::getName)
						.thenComparing(Cinema::getLinkName)
		);
		model.addAttribute("cinemas", cinemas);
		model.addAttribute("cinemasByManager", cinemaService.findAllByManager(userService.findByUsername(currentUsername).get()));
		return "cinema/list";
	}

	@DeleteMapping("/{link}")
	public String deleteCinema(@PathVariable String link, RedirectAttributes redirectAttributes) {
		if (cinemaService.existsByLinkName(link)) {
			cinemaService.deleteByLinkName(link);
			if (cinemaService.existsByLinkName(link)) {
				redirectAttributes.addFlashAttribute("errorDescription", "Удалить кинотеатр не получилось.");
			} else {
				redirectAttributes.addFlashAttribute("successDescription", "Кинотеатр успешно удалён.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого кинотеатра нет.");
		}

		return "redirect:/cinemas";
	}

	@GetMapping("/{link}")
	public String showCinemaDetails(@PathVariable String link, Model model, RedirectAttributes redirectAttributes) {
		Optional<Cinema> cinema = cinemaService.findByLinkName(link);

		if (cinema.isPresent()) {
			model.addAttribute("cinema", cinema.get());
			return "cinema/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого кинотеатра нет.");
			return "redirect:/cinemas";
		}
	}
}
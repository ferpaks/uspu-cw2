package ferpaks.cinema.controller;

import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.City;
import ferpaks.cinema.entity.Hall;
import ferpaks.cinema.entity.Seat;
import ferpaks.cinema.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/halls")
@RequiredArgsConstructor
public class HallController {
	private final HallService hallService;
	private final CinemaService cinemaService;
	private final CinemaUserService userService;
	private final SessionService sessionService;

	@GetMapping
	public String showHallList(Model model, Authentication authentication) {
		String currentUsername = authentication.getName();

		List<Hall> halls = hallService.findAll();
		halls.sort(Comparator.comparing((Hall hall) -> hall.getCinema().getAddress().getCity().getName())
				.thenComparing(hall -> hall.getCinema().getName())
				.thenComparing(Hall::getName));
		model.addAttribute("halls", halls);
		model.addAttribute("hallsByManager", hallService.findAllByManager(userService.findByUsername(currentUsername).get()));
		return "hall/list";
	}

	@GetMapping("/{id}")
	public String showHallDetail(@PathVariable Long id, Model model) {
		Hall hall = hallService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid hall ID: " + id));

		Cinema cinema = hall.getCinema();
		int totalSeats = hall.getSeats().size();

		Map<Integer, List<Seat>> seatsGroupedByRow = hall.getSeats().stream()
				.collect(Collectors.groupingBy(Seat::getRowNumber));

		model.addAttribute("hall", hall);
		model.addAttribute("cinemaName", cinema.getName());
		model.addAttribute("totalSeats", totalSeats);
		model.addAttribute("seatsGroupedByRow", seatsGroupedByRow);
		return "hall/detail";
	}

	@GetMapping("/new")
	public String showAddHallForm(Model model, Authentication authentication) {
		String currentUsername = authentication.getName();

		model.addAttribute("hall", new Hall());
		model.addAttribute("cinemas", cinemaService.findAll());
		model.addAttribute("cinemasByManager", cinemaService.findAllByManager(userService.findByUsername(currentUsername).get()));

		return "hall/add";
	}

	@PostMapping
	public String addHall(@ModelAttribute Hall hall, @RequestParam Long cinemaId, @RequestParam int rows, @RequestParam int columns, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("cinemas", cinemaService.findAll());
			return "hall/add";
		}
		hallService.saveWithSeats(hall, cinemaId, rows, columns);
		return "redirect:/halls";
	}


	@GetMapping("/{id}/edit")
	public String showEditHallForm(@PathVariable Long id, Model model, Authentication authentication) {
		String currentUsername = authentication.getName();

		Hall hall = hallService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid hall ID: " + id));

		Cinema cinema = hall.getCinema();
		int totalSeats = hall.getSeats().size();

		Map<Integer, List<Seat>> seatsGroupedByRow = hall.getSeats().stream()
				.collect(Collectors.groupingBy(Seat::getRowNumber));

		model.addAttribute("hall", hall);
		model.addAttribute("cinemas", cinemaService.findAll());
		model.addAttribute("totalSeats", totalSeats);
		model.addAttribute("sessionsByThisHall", sessionService.findAllByHall(hall));
		model.addAttribute("seatsGroupedByRow", seatsGroupedByRow);
		model.addAttribute("cinemasByManager", cinemaService.findAllByManager(userService.findByUsername(currentUsername).get()));

		return "hall/edit";
	}

	@PutMapping("/{id}")
	public String editHall(@PathVariable Long id,
						   @ModelAttribute Hall hall,
						   BindingResult result,
						   @RequestParam String name,
						   @RequestParam Long cinemaId,
						   @RequestParam int rows,
						   @RequestParam int columns,
						   Model model) {

		if (result.hasErrors()) {
			model.addAttribute("cinemas", cinemaService.findAll());
			return "hall/edit";
		}

		//Hall hall = hallService.findById(id)
		//		.orElseThrow(() -> new IllegalArgumentException("Invalid hall ID: " + id));
		Cinema cinema = cinemaService.findById(cinemaId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid cinema ID: " + cinemaId));

		hall.setName(name);
		hall.setCinema(cinema);

		// Update hall with new seats configuration
		hallService.updateHallWithSeats(id, name, cinemaId, rows, columns);

		return "redirect:/halls";
	}

	@DeleteMapping("/{id}")
	public String deleteHall(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (hallService.existsById(id)) {
			hallService.deleteById(id);
			if (hallService.existsById(id)) {
				redirectAttributes.addFlashAttribute("errorDescription", "Удалить зал не получилось.");
			} else {
				redirectAttributes.addFlashAttribute("successDescription", "Зал успешно удалён.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого зала нет.");
		}

		return "redirect:/halls";
	}
}
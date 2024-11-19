package ferpaks.cinema.controller;

import ferpaks.cinema.entity.City;
import ferpaks.cinema.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
	private final CityService cityService;

	@GetMapping
	public String showCities(Model model) {
		List<City> cities = cityService.findAll();
		cities.sort(Comparator.comparing(City::getName));
		model.addAttribute("cities", cities);
		return "city/list";
	}

	@GetMapping("/new")
	public String showAddCityForm(Model model) {
		model.addAttribute("city", new City());
		return "city/add";
	}

	@PostMapping
	public String addCity(@ModelAttribute City city, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "city/add";
		}
		cityService.save(city);
		return "redirect:/cities";
	}

	@GetMapping("/{id}/edit")
	public String showEditCityForm(@PathVariable Long id, Model model) {
		City city = cityService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid city ID: " + id));
		model.addAttribute("city", city);
		return "city/edit";
	}

	@PutMapping("/{id}")
	public String editCity(@PathVariable Long id, @ModelAttribute City city, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "city/edit";
		}

		cityService.updateCity(id, city);
		return "redirect:/cities";
	}

	@GetMapping("/{id}")
	public String showCityDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<City> city = cityService.findById(id);

		if (cityService.existsById(id) && city.isPresent()) {
			model.addAttribute("city", city.get());
			return "city/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого города нет.");
		}

		return "redirect:/cities";
	}

	@DeleteMapping("/{id}")
	public String deleteCity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (cityService.existsById(id)) {
			cityService.deleteById(id);
			if (cityService.existsById(id)) {
				redirectAttributes.addFlashAttribute("errorDescription", "Удалить город не получилось.");
			} else {
				redirectAttributes.addFlashAttribute("successDescription", "Город успешно удалён.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого города нет.");
		}

		return "redirect:/cities";
	}
}
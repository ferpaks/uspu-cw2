package ferpaks.cinema.controller;

import ferpaks.cinema.entity.City;
import ferpaks.cinema.entity.Street;
import ferpaks.cinema.entity.StreetType;
import ferpaks.cinema.service.CityService;
import ferpaks.cinema.service.StreetService;
import ferpaks.cinema.service.StreetTypeService;
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
@RequestMapping("/streets")
@RequiredArgsConstructor
public class StreetController {
	private final StreetService streetService;
	private final CityService cityService;
	private final StreetTypeService streetTypeService;

	@GetMapping
	public String showStreetList(Model model) {
		List<Street> streets = streetService.findAll();
		streets.sort(
				Comparator.comparing((Street street) -> street.getCity().getName())
						.thenComparing(Street::getName)
						.thenComparing(street -> street.getType().getFullName())
		);
		model.addAttribute("streets", streets);
		return "street/list";
	}

	@GetMapping("/new")
	public String showAddStreetForm(Model model) {
		model.addAttribute("street", new Street());
		model.addAttribute("cities", cityService.findAll());
		model.addAttribute("streetTypes", streetTypeService.findAll());
		return "street/add";
	}

	@PostMapping
	public String addStreet(@ModelAttribute Street street, @RequestParam Long cityId, @RequestParam Long typeId, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("cities", cityService.findAll());
			model.addAttribute("streetTypes", streetTypeService.findAll());
			return "street/add";
		}

		City city = cityService.findById(cityId)
				.orElseThrow(() -> new IllegalArgumentException("Некорректный ID города"));
		StreetType streetType = streetTypeService.findById(typeId)
				.orElseThrow(() -> new IllegalArgumentException("Некорректный ID типа улицы"));

		if (streetService.existsByName(street.getName(), city, streetType)) {
			redirectAttributes.addFlashAttribute("errorDescription", "Такая улица уже существует.");
			return "redirect:/streets";
		} else {
			street.setCity(city);
			street.setType(streetType);
			streetService.save(street);
			return "redirect:/streets";
		}

	}

	@GetMapping("/{id}/edit")
	public String showEditStreetForm(@PathVariable Long id, Model model) {
		Street street = streetService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid street ID: " + id));
		model.addAttribute("street", street);
		model.addAttribute("cities", cityService.findAll());
		model.addAttribute("streetTypes", streetTypeService.findAll());
		return "street/edit";
	}

	@GetMapping("/{id}")
	public String showStreetDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Street> street = streetService.findById(id);

		if (streetService.existsById(id) && street.isPresent()) {
			model.addAttribute("street", street.get());
			return "street/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такой улицы нет.");
			return "redirect:/streets";
		}
	}

	@PutMapping("/{id}")
	public String editStreet(@PathVariable Long id, @ModelAttribute Street street, @RequestParam Long cityId, @RequestParam Long typeId, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("cities", cityService.findAll());
			model.addAttribute("streetTypes", streetTypeService.findAll());
			return "street/edit";
		}

		Street existingStreet = streetService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid street ID: " + id));

		City city = cityService.findById(cityId)
				.orElseThrow(() -> new IllegalArgumentException("City not found"));

		StreetType streetType = streetTypeService.findById(typeId)
				.orElseThrow(() -> new IllegalArgumentException("Street type not found"));


		if (streetService.existsByName(street.getName(), city, streetType)) {
			redirectAttributes.addFlashAttribute("errorDescription", "Такая улица уже существует.");
			return "redirect:/streets";
		} else {
			existingStreet.setName(street.getName());
			existingStreet.setCity(city);
			existingStreet.setType(streetType);
			streetService.updateStreet(id, existingStreet);
			return "redirect:/streets";
		}
	}

	@DeleteMapping("/{id}")
	public String deleteStreet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (streetService.existsById(id)) {
			streetService.deleteById(id);
			if (streetService.existsById(id)) {
				redirectAttributes.addFlashAttribute("errorDescription", "Удалить улицу не получилось.");
			} else {
				redirectAttributes.addFlashAttribute("successDescription", "Улица успешно удалена.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такой улицы нет.");
		}

		return "redirect:/streets";
	}
}

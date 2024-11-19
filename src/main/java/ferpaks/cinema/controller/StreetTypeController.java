package ferpaks.cinema.controller;

import ferpaks.cinema.entity.City;
import ferpaks.cinema.entity.StreetType;
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
@RequestMapping("/street-types")
@RequiredArgsConstructor
public class StreetTypeController {
	private final StreetTypeService streetTypeService;

	@GetMapping("/new")
	public String showAddStreetTypeForm(Model model) {
		model.addAttribute("streetType", new StreetType());
		return "street-type/add";
	}

	@PostMapping
	public String addStreetType(@ModelAttribute StreetType streetType, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "street-type/add";
		}
		streetTypeService.save(streetType);
		return "redirect:/street-types";
	}

	@GetMapping("/{id}/edit")
	public String showEditStreetTypeForm(@PathVariable Long id, Model model) {
		StreetType streetType = streetTypeService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid street type ID: " + id));
		model.addAttribute("streetType", streetType);
		return "street-type/edit";
	}

	@PutMapping("/{id}")
	public String editStreetType(@PathVariable Long id, @ModelAttribute StreetType streetType, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "street-type/edit";
		}
		streetTypeService.updateStreetType(id, streetType);
		return "redirect:/street-types";
	}

	@GetMapping
	public String showStreetTypes(Model model) {
		List<StreetType> streetTypes = streetTypeService.findAll();
		streetTypes.sort(Comparator.comparing(StreetType::getFullName));
		model.addAttribute("streetTypes", streetTypes);
		return "street-type/list";
	}

	@GetMapping("/{id}")
	public String showStreetTypeDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<StreetType> streetType = streetTypeService.findById(id);

		if (streetTypeService.existsById(id) && streetType.isPresent()) {
			model.addAttribute("streetType", streetType.get());
			return "street-type/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого типа улицы нет.");
		}

		return "redirect:/street-types";
	}

	@DeleteMapping("/{id}")
	public String deleteStreetType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (streetTypeService.existsById(id)) {
			streetTypeService.deleteById(id);
			if (streetTypeService.existsById(id)) {
				redirectAttributes.addFlashAttribute("errorDescription", "Удалить тип улицы не получилось.");
			} else {
				redirectAttributes.addFlashAttribute("successDescription", "Тип улицы успешно удалён.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого типа улицы нет.");
		}
		return "redirect:/street-types";
	}
}
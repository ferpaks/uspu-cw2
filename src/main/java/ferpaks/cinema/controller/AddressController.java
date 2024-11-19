package ferpaks.cinema.controller;

import ferpaks.cinema.entity.Address;
import ferpaks.cinema.entity.Street;
import ferpaks.cinema.service.AddressService;
import ferpaks.cinema.service.CityService;
import ferpaks.cinema.service.StreetService;
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
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
	private final AddressService addressService;
	private final CityService cityService;
	private final StreetService streetService;

	@GetMapping
	public String showList(Model model) {
		List<Address> addresses = addressService.findAll();
		addresses.sort(
				Comparator.comparing((Address address) -> address.getCity().getName())
						.thenComparing(address -> address.getStreet().getName())
						.thenComparing(Address::getHouse)
		);
		model.addAttribute("addresses", addresses);
		return "address/list";
	}

	@GetMapping("/new")
	public String showAddAddressForm(Model model) {
		model.addAttribute("address", new Address());
		model.addAttribute("cities", cityService.findAll());
		List<Street> streets = streetService.findAll();
		streets.sort(Comparator.comparing(Street::getName));
		model.addAttribute("streets", streets);
		return "address/add";
	}

	@PostMapping
	public String addAddress(@ModelAttribute Address address, @RequestParam Long streetId, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("cities", cityService.findAll());
			model.addAttribute("streets", streetService.findAll());
			return "address/add";
		}

		Street street = streetService.findById(streetId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid street ID: " + streetId));
		address.setStreet(street);

		try {
			addressService.saveAddress(address);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorDescription", e.getMessage());
		}
		return "redirect:/addresses";
	}

	@GetMapping("/{id}/edit")
	public String showEditAddressForm(@PathVariable Long id, Model model) {
		Address address = addressService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid address ID: " + id));
		model.addAttribute("address", address);
		model.addAttribute("cities", cityService.findAll());
		model.addAttribute("streets", streetService.findAll());
		return "address/edit";
	}

	@PutMapping("/{id}")
	public String editAddress(@PathVariable Long id, @ModelAttribute Address address, @RequestParam Long streetId, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("cities", cityService.findAll());
			model.addAttribute("streets", streetService.findAll());
			return "address/edit";
		}

		Street street = streetService.findById(streetId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid street ID: " + id));

		address.setStreet(street);
		try {
			addressService.updateAddress(id, address);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorDescription", e.getMessage());
		}
		return "redirect:/addresses";
	}

	@DeleteMapping("/{id}")
	public String deleteAddress(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (addressService.existsById(id)) {
			addressService.deleteById(id);
			if (addressService.existsById(id)) {
				redirectAttributes.addFlashAttribute("errorDescription", "Удалить адрес не получилось.");
			} else {
				redirectAttributes.addFlashAttribute("successDescription", "Адрес успешно удалён.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого адреса нет.");
		}

		return "redirect:/addresses";
	}

	@GetMapping("/{id}")
	public String showAddressDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Address> address = addressService.findById(id);

		if (addressService.existsById(id) && address.isPresent()) {
			model.addAttribute("address", address.get());
			return "address/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого адреса нет.");
			return "redirect:/addresses";
		}
	}
}
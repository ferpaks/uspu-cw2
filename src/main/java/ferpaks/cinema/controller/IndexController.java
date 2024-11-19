package ferpaks.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {
	@GetMapping
	public String showIndexPage() {
		return "index";
	}

	@GetMapping("/index")
	public String redirectToIndexPage() {
		return "redirect:/";
	}

	@GetMapping("/functionality")
	public String showFunctionalityPage() {
		return "functionality";
	}
}
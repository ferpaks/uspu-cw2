package ferpaks.cinema.controller;

import ferpaks.cinema.entity.Genre;
import ferpaks.cinema.entity.Movie;
import ferpaks.cinema.entity.Rating;
import ferpaks.cinema.repository.GenreRepository;
import ferpaks.cinema.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
	private final MovieService movieService;
	private final GenreRepository genreRepository;
	private final String postersDir = "data/posters";

	@GetMapping("/new")
	public String showAddMovieForm(Model model) {
		model.addAttribute("movie", new Movie());
		model.addAttribute("allGenres", genreRepository.findAll());
		model.addAttribute("ratings", Rating.values());
		return "movie/add";
	}

	@PostMapping
	public String addMovie(@ModelAttribute("movie") Movie movie, /*@RequestParam("posterFile") MultipartFile posterFile,*/
						    @RequestParam("genreIds") List<Long> genreIds, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "movie/add";
		}

		// TODO Проверить, чтобы серверная логика корректно обрабатывала случаи, когда ни один чекбокс не выбран (массив будет пустым?)

		/*
		if (!posterFile.isEmpty()) {
			String posterPath = movieService.savePosterFile(posterFile);
			movie.setPosterUrl(posterPath);
		} else {
			model.addAttribute("error", "Плакат обязателен для добавления фильма");
			return "movie/add";
		}
		 */
		Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));

		movie.setGenres(genres);

		movieService.addMovie(movie);

		return "redirect:/movies";
	}

	@GetMapping("/{movieLink}/edit")
	public String showEditMovieForm(@PathVariable String movieLink, Model model) {
		Movie movie = movieService.findByLinkTitle(movieLink)
				.orElseThrow(() -> new IllegalArgumentException("Фильм с таким ID не найден"));

		/*
		model.addAttribute("movie", movie);
		File folder = new File(postersDir);
		String[] files = folder.exists() ? folder.list() : new String[0];
		model.addAttribute("posterFiles", files);
		 */

		model.addAttribute("movie", movie);
		model.addAttribute("allGenres", genreRepository.findAll());
		model.addAttribute("durationInMinutes", movie.getDuration().toMinutes());
		model.addAttribute("allRatings", Rating.values());
	//	model.addAttribute("availablePosters", movieService.getAvailablePosters());

		return "movie/edit";
	}

	@PutMapping("/{movieLink}")
	public String editMovie(@PathVariable String movieLink, @ModelAttribute("movie") @Valid Movie movie,
							/*@RequestParam("posterFile") MultipartFile posterFile,*/
							BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "movie/edit";
		}

		/*
		if (!posterFile.isEmpty()) {
			String posterPath = movieService.savePosterFile(posterFile);
			movie.setPosterUrl(posterPath);
		}
		 */

		movieService.updateMovie(movieLink, movie);
		return "redirect:/movies";
	}

	@DeleteMapping("/{movieLink}")
	public String deleteMovie(@PathVariable String movieLink) {
		movieService.deleteMovie(movieLink);
		return "redirect:/movies";
	}

	@PostMapping("/{movieLink}/archive")
	public String archiveMovie(@PathVariable String movieLink) {
		movieService.archiveMovie(movieLink);
		return "redirect:/movies";
	}

	@PostMapping("/{movieLink}/unarchive")
	public String unarchiveMovie(@PathVariable String movieLink) {
		movieService.unarchiveMovie(movieLink);
		return "redirect:/movies";
	}

	@GetMapping
	public String showList(Model model) {
		model.addAttribute("movies", movieService.findAll());
		return "movie/list";
	}

	@GetMapping("/{movieLink}")
	public String showDetail(@PathVariable String movieLink, Model model, RedirectAttributes redirectAttributes) {
		Optional<Movie> movie = movieService.findByLinkTitle(movieLink);

		if (movie.isPresent()) {
			model.addAttribute("movie", movie.get());
			return "movie/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого фильма нет.");
			return "redirect:/movies";
		}
	}
}
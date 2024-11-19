package ferpaks.cinema.service;

import ferpaks.cinema.entity.Movie;
import ferpaks.cinema.entity.MovieStatus;
import ferpaks.cinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
	private final MovieRepository movieRepository;

	private static final String POSTER_DIRECTORY = "data/posters/";

	public Movie addMovie(Movie movie) {
		movie.setStatus(MovieStatus.ACTIVE);
		return movieRepository.save(movie);
	}

	public Optional<Movie> findByLinkTitle(String linkTitle) {
		return movieRepository.findByLinkTitle(linkTitle);
	}

	public Movie updateMovie(Long id, Movie updatedMovie) {
		Movie existingMovie = movieRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Фильм с таким ID не найден"));

		existingMovie.setTitle(updatedMovie.getTitle());
		existingMovie.setLinkTitle(updatedMovie.getLinkTitle());
		existingMovie.setDescription(updatedMovie.getDescription());
		existingMovie.setDuration(updatedMovie.getDuration());
		existingMovie.setGenres(updatedMovie.getGenres());
		existingMovie.setRating(updatedMovie.getRating());
	//	existingMovie.setPosterUrl(updatedMovie.getPosterUrl());
		existingMovie.setBasePriceInRubles(updatedMovie.getBasePriceInRubles());

		return movieRepository.save(existingMovie);
	}

	public Movie updateMovie(String linkTitle, Movie updatedMovie) {
		Movie existingMovie = movieRepository.findByLinkTitle(linkTitle)
				.orElseThrow(() -> new IllegalArgumentException("Фильм с такой ссылкой не найден"));

		existingMovie.setTitle(updatedMovie.getTitle());
		existingMovie.setLinkTitle(updatedMovie.getLinkTitle());
		existingMovie.setDescription(updatedMovie.getDescription());
		existingMovie.setDuration(updatedMovie.getDuration());
		existingMovie.setGenres(updatedMovie.getGenres());
		existingMovie.setRating(updatedMovie.getRating());
	//	existingMovie.setPosterUrl(updatedMovie.getPosterUrl());
		existingMovie.setBasePriceInRubles(updatedMovie.getBasePriceInRubles());

		return movieRepository.save(existingMovie);
	}

	@Transactional
	public void deleteMovie(String linkTitle) {
		movieRepository.deleteByLinkTitle(linkTitle);
	}

	public Movie archiveMovie(String linkTitle) {
		Movie movie = movieRepository.findByLinkTitle(linkTitle)
				.orElseThrow(() -> new IllegalArgumentException("Фильм с такой ссылкой не найден"));
		movie.setStatus(MovieStatus.ARCHIVED);
		return movieRepository.save(movie);
	}

	public Movie unarchiveMovie(String linkTitle) {
		Movie movie = movieRepository.findByLinkTitle(linkTitle)
				.orElseThrow(() -> new IllegalArgumentException("Фильм с такой ссылкой не найден"));
		movie.setStatus(MovieStatus.ACTIVE);
		return movieRepository.save(movie);
	}

	public Optional<Movie> findById(Long id) {
		return movieRepository.findById(id);
	}

	public List<Movie> findAll() {
		return movieRepository.findAll();
	}

	public String savePosterFile(MultipartFile file) {
		try {
			String projectPath = System.getProperty("user.dir");
			String directoryPath = POSTER_DIRECTORY;
			File project = new File(projectPath);
			File directory = new File(project.getAbsolutePath() + directoryPath);

			if (!directory.exists()) {
				directory.mkdirs();
			}

			String filePath = directoryPath + file.getOriginalFilename();
			file.transferTo(new File(filePath));

			return filePath;
		} catch (IOException e) {
			throw new RuntimeException("Ошибка при сохранении файла: " + e.getMessage(), e);
		}
	}

	public List<String> getAvailablePosters() {
		File folder = new File(POSTER_DIRECTORY);

		String[] files = folder.list((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)"));

		if (files != null) {
			return Arrays.stream(files)
					.map(file -> POSTER_DIRECTORY + file)
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}
}
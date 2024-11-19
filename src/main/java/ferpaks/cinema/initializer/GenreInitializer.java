package ferpaks.cinema.initializer;

import ferpaks.cinema.entity.Genre;
import ferpaks.cinema.repository.GenreRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreInitializer {
	private final GenreRepository genreRepository;

	@PostConstruct
	public void init() {
		if (genreRepository.count() == 0) {
			List<Genre> genres = List.of(
					new Genre(null, "Боевик"),
					new Genre(null, "Мультфильм"),
					new Genre(null, "Комедия"),
					new Genre(null, "Документальный"),
					new Genre(null, "Драма"),
					new Genre(null, "Фэнтези"),
					new Genre(null, "Исторический"),
					new Genre(null, "Ужасы"),
					new Genre(null, "Романтика"),
					new Genre(null, "Научная фантастика"),
					new Genre(null, "Триллер"),
					new Genre(null, "Вестерн")
			);

			genreRepository.saveAll(genres);
			System.out.println("Инициализация жанров завершена.");
		}
	}
}
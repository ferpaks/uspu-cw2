package ferpaks.cinema.repository;

import ferpaks.cinema.entity.Movie;
import ferpaks.cinema.entity.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	List<Movie> findByStatus(MovieStatus status);
	Optional<Movie> findByLinkTitle(String linkTitle);

	void deleteByLinkTitle(String linkTitle);
}
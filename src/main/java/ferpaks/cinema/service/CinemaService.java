package ferpaks.cinema.service;

import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.City;
import ferpaks.cinema.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CinemaService {
	private final CinemaRepository cinemaRepository;

	public List<Cinema> findAll() {
		return cinemaRepository.findAll();
	}

	public List<Cinema> findAllByManager(CinemaUser manager) {
		return cinemaRepository.findAllByManager(manager);
	}

	public Optional<Cinema> findByLinkName(String linkName) {
		return cinemaRepository.findByLinkName(linkName);
	}
	public Optional<Cinema> findById(Long id) {
		return cinemaRepository.findById(id);
	}

	public void deleteByLinkName(String linkName) {
		cinemaRepository.deleteByLinkName(linkName);
	}
	public List<Cinema> findByCity(City city) {
		return cinemaRepository.findByAddressStreetCity(city);
	}

	public Cinema save(Cinema cinema) {
		if (cinemaRepository.existsByLinkName(cinema.getLinkName())) {
			throw new IllegalArgumentException("Cinema with this link already exists.");
		}

		return cinemaRepository.save(cinema);
	}

	public void update(String link, Cinema updatedCinema) {
		Cinema existingCinema = cinemaRepository.findByLinkName(link)
				.orElseThrow(() -> new IllegalArgumentException("Cinema not found"));

		existingCinema.setName(updatedCinema.getName());
		existingCinema.setLinkName(updatedCinema.getLinkName());
		existingCinema.setStatus(updatedCinema.getStatus());
		existingCinema.setAddress(updatedCinema.getAddress());
		existingCinema.setManager(updatedCinema.getManager());

		cinemaRepository.save(existingCinema);
	}

	public boolean existsByAddressId(Long addressId) {
		return cinemaRepository.existsByAddressId(addressId);
	}

	public boolean existsByLinkName(String linkName) {
		return cinemaRepository.existsByLinkName(linkName);
	}

}
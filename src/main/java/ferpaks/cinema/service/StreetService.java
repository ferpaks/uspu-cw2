package ferpaks.cinema.service;

import ferpaks.cinema.entity.City;
import ferpaks.cinema.entity.Street;
import ferpaks.cinema.entity.StreetType;
import ferpaks.cinema.repository.CityRepository;
import ferpaks.cinema.repository.StreetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreetService {
	private final StreetRepository streetRepository;
	private final CityRepository cityRepository;


	public List<Street> findAll() {
		return streetRepository.findAll();
	}

	public Optional<Street> findById(Long id) {
		return streetRepository.findById(id);
	}

	public boolean existsById(Long id) {
		return streetRepository.existsById(id);
	}

	public boolean existsByName(String name, City city, StreetType type) {
		return streetRepository.existsByCityAndNameAndType(city, name, type);
	}

	public Street save(Street street) {
		return streetRepository.save(street);
	}

	public void deleteById(Long id) {
		streetRepository.deleteById(id);
	}

	public void updateStreet(Long streetId, Street street) {
		streetRepository.save(street);
	}
}
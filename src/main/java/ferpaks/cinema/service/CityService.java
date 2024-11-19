package ferpaks.cinema.service;

import ferpaks.cinema.entity.City;
import ferpaks.cinema.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityService {
	private final CityRepository cityRepository;

	public List<City> findAll() {
		return cityRepository.findAll();
	}

	public Optional<City> findById(Long id) {
		return cityRepository.findById(id);
	}

	public boolean existsById(Long id) {
		return cityRepository.existsById(id);
	}

	public City save(City city) {
		return cityRepository.save(city);
	}

	public void deleteById(Long id) {
		cityRepository.deleteById(id);
	}

	public void updateCity(Long id, City city) {
		City existingCity = cityRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("City not found"));
		existingCity.setName(city.getName());
		cityRepository.save(existingCity);
	}
}
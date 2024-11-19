package ferpaks.cinema.service;

import ferpaks.cinema.entity.StreetType;
import ferpaks.cinema.repository.StreetTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreetTypeService {
	private final StreetTypeRepository streetTypeRepository;

	public List<StreetType> findAll() {
		return streetTypeRepository.findAll();
	}

	public Optional<StreetType> findById(Long id) {
		return streetTypeRepository.findById(id);
	}

	public StreetType save(StreetType streetType) {
		return streetTypeRepository.save(streetType);
	}

	public void deleteById(Long id) {
		streetTypeRepository.deleteById(id);
	}

	public boolean existsById(Long id) {
		return streetTypeRepository.existsById(id);
	}

	public void updateStreetType(Long id, StreetType streetType) {
		StreetType existingStreetType = streetTypeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("StreetType not found"));
		existingStreetType.setFullName(streetType.getFullName());
		existingStreetType.setShortName(streetType.getShortName());
		streetTypeRepository.save(existingStreetType);
	}
}
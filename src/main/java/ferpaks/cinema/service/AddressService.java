package ferpaks.cinema.service;

import ferpaks.cinema.entity.Address;
import ferpaks.cinema.repository.AddressRepository;
import ferpaks.cinema.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
	private final AddressRepository addressRepository;
	private final CinemaRepository cinemaRepository;

	public List<Address> findAll() {
		return addressRepository.findAll();
	}

	public Optional<Address> findById(Long id) {
		return addressRepository.findById(id);
	}

	public Address saveAddress(Address address) {
		if (addressRepository.existsByStreetAndHouse(address.getStreet(), address.getHouse())) {
			throw new IllegalArgumentException("Такой адрес уже существует.");
		}

		return addressRepository.save(address);
	}

	public void updateAddress(Long id, Address updatedAddress) {
		Address existingAddress = addressRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Адрес не найден."));
		if (addressRepository.existsByStreetAndHouse(updatedAddress.getStreet(), updatedAddress.getHouse())) {
			throw new IllegalArgumentException("Такой адрес уже существует.");
		} else {
			existingAddress.setStreet(updatedAddress.getStreet());
			existingAddress.setHouse(updatedAddress.getHouse());
			addressRepository.save(existingAddress);
		}
	}

	public List<Address> findAvailableAddresses() {
		List<Address> allAddresses = addressRepository.findAll();
		List<Address> occupiedAddresses = cinemaRepository.findAllOccupiedAddresses();

		allAddresses.removeAll(occupiedAddresses);

		return allAddresses;
	}

	public boolean existsById(Long id) {
		return addressRepository.existsById(id);
	}

	public void deleteById(Long id) {
		addressRepository.deleteById(id);
	}
}
package com.wesave.back;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarRessource {

	@Autowired
	private CarRepository CarRepository;

	@GetMapping("/cars")
	public List<Car> all() {
		return CarRepository.findAll();
	}

	@GetMapping("/cars/{id}")
	public Car retrieveCar(@PathVariable Long id) {
		return CarRepository.findById(id);
	}

	@PostMapping("/cars")
	public Car newCar(@RequestBody Car Car) {

		return CarRepository.save(Car);

	}

}

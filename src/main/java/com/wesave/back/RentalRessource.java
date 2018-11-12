package com.wesave.back;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wesave.back.DateFormatException;

@RestController
public class RentalRessource {

	@Autowired
	private RentalRepository RentalRepository;

	@Autowired
	private CarRepository CarRepository;

	@GetMapping("/rentals")
	public List<Rental> all() {
		return RentalRepository.findAll();
	}

	@PostMapping("/rentals")
	public Rental newRental(@RequestBody Rental rental) {

		int price = 0;

		Optional<Car> car = CarRepository.findById(rental.getCar_id());

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


		try {
			Date startDate = formatter.parse(rental.getStart_date());

			Date endDate = formatter.parse(rental.getEnd_date());

			long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
			long nbDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

			price += (car.get().getPrice_per_day() * nbDays) + car.get().getPrice_per_km() * rental.getDistance();

			rental.setPrice(price);

			Rental savedRental = RentalRepository.save(rental);

			return savedRental;

		} catch (ParseException e) {
			e.printStackTrace();
			throw new DateFormatException();
		}

	}
}

package com.wesave.back;

import java.util.List;

public class Input {

	private List<Car> cars;
	private List<Rental> rentals;

	public Input(List<Car> cars, List<Rental> rentals) {
		this.cars = cars;
		this.rentals = rentals;
	}


	public List<Car> getCars() {
		return cars;
	}
	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
	public List<Rental> getRentals() {
		return rentals;
	}
	public void setRentals(List<Rental> rentals) {
		this.rentals = rentals;
	}

}

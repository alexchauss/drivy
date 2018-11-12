package com.wesave.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wesave.back.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

	Car findById(Long id);
}

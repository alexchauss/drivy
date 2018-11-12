package com.wesave.back;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wesave.back.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

	List<Rental> findById(String catId);
}
package com.wesave.back;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Rental {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	private int car_id;

    private String start_date;

    private String end_date;

    private int distance;

    private int price;

	protected Rental() {}

	public Rental(int car_id, String start_date, String end_date, int distance) {
		this.car_id = car_id;
		this.start_date = start_date;
		this.end_date = end_date;
		this.distance = distance;
    }

	@Override
    public String toString() {
        return String.format(
                "Rental[id=%s,carId=%s,start_date=%s,start_date=%s,distance=%s]",
                id,car_id,start_date,end_date,distance);
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getCar_id() {
		return car_id;
	}

	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}




}

package com.wesave.back;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Car {

	@Id
    private Integer id;

	private int price_per_day;

    private int price_per_km;

	protected Car() {}

	public Car(int price_per_day, int price_per_km) {
		this.price_per_day = price_per_day;
		this.price_per_km = price_per_km;
    }

	@Override
    public String toString() {
        return String.format(
                "Car[id='%s',price_per_day=%s,price_per_km=%s]",
                id,price_per_day,price_per_km);
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getPrice_per_day() {
		return price_per_day;
	}

	public void setPrice_per_day(int price_per_day) {
		this.price_per_day = price_per_day;
	}

	public int getPrice_per_km() {
		return price_per_km;
	}

	public void setPrice_per_km(int price_per_km) {
		this.price_per_km = price_per_km;
	}




}

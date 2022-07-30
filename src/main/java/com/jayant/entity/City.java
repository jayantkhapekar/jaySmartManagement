package com.jayant.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CITY")
public class City {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cityId;
	private String cityName;

	public City() {
		super();
	}
	
	

	public City(int cityId, String cityName) {
		super();
		this.cityId = cityId;
		this.cityName = cityName;
	}



	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public String toString() {
		return "City [cityId=" + cityId + ", cityName=" + cityName + "]";
	}

	
	
}

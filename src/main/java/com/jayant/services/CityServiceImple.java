package com.jayant.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jayant.entity.City;

@Service

public class CityServiceImple implements CityService {

	List<City> cityList;

	public CityServiceImple() {

		cityList = new ArrayList<>();
		cityList.add(new City(1, "Nagpur"));
		cityList.add(new City(2, "Pune"));
		cityList.add(new City(3, "Mumbai"));
		cityList.add(new City(4, "Banglore"));
		cityList.add(new City(5, "Chennai"));
		cityList.add(new City(6, "Amravati"));

	}

	@Override
	public List<City> getAllCity() {

		return cityList;
	}

}

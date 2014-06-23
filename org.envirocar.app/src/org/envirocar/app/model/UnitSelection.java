package org.envirocar.app.model;

import java.io.Serializable;

public class UnitSelection implements Serializable{

	private static final long serialVersionUID = 1L;
	private String speed_first="kilometre(km)";
	private String speed_second="hour(h)";
	private String co2_first="kilogram(kg)";
	private String co2_second="hour(h)";
	private String fuel_first="litre(l)";
	private String fuel_second="hour(h)";

	public UnitSelection(String speed_first,String speed_second,String co2_first,String co2_second,String fuel_first,String fuel_second){

		this.speed_first=speed_first;
		this.speed_second=speed_second;
		this.co2_first=co2_first;
		this.co2_second=co2_second;
		this.fuel_first=fuel_first;
		this.fuel_second=fuel_second;

	}


	public UnitSelection(){

		this.speed_first="from_preferences";
		this.speed_second="from_preferences";
		this.co2_first="from_preferences";
		this.co2_second="from_preferences";
		this.fuel_first="from_preferences";
		this.fuel_second="from_preferences";
	}

	//when preference is changed, you need to store the default values for the corresponding language

	public void setSpeed_first(String speed_first){

		this.speed_first=speed_first;
	}

	public void setSpeed_second(String speed_second){

		this.speed_second=speed_second;
	}

	public void setCo2_first(String co2_first){

		this.co2_first=co2_first;
	}

	public void setCo2_second(String co2_second){

		this.co2_second=co2_second;
	}

	public void setFuel_first(String fuel_first){

		this.fuel_first=fuel_first;
	}

	public void setFuel_second(String fuel_second){

		this.fuel_second=fuel_second;
	}




	public String getSpeed_first(){

		return this.speed_first;
	}

	public String getSpeed_second(){

		return this.speed_second;
	}
	public String getCo2_first(){

		return this.co2_first;
	}

	public String getCo2_second(){

		return this.co2_second;
	}

	public String getFuel_first(){

		return this.fuel_first;
	}

	public String getFuel_second(){

		return this.fuel_second;
	}




	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof UnitSelection) {
			UnitSelection u = (UnitSelection) o;
			result = this.speed_first.equals(u.speed_first) 
					&& this.speed_second.equals(u.speed_second)
					&& this.co2_first.equals(u.co2_first)
					&& this.co2_second.equals(u.co2_second)
					&& this.fuel_first.equals(u.fuel_first)
					&& this.fuel_second.equals(u.fuel_second);
		}
		return result;
	}





}

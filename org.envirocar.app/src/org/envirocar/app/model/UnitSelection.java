package org.envirocar.app.model;

import java.io.Serializable;

public class UnitSelection implements Serializable{

	private static final long serialVersionUID = 1L;
	private String speed=null;
	private String co2_emission=null;
	private String fuel_consumption=null;
	

	public UnitSelection(String speed,String co2_emission,String fuel_consumption){

		this.speed=speed;
		this.co2_emission=co2_emission;
		this.fuel_consumption=fuel_consumption;
		

	}


	public UnitSelection(){
		
	}

	//when preference is changed, you need to store the default values for the corresponding language

	public void setSpeed(String speed){

		this.speed=speed;
	}

	public void setCo2Emission(String co2_emission){

		this.co2_emission=co2_emission;
	}

	public void setFuelConsumption(String fuel_consumption){

		this.fuel_consumption=fuel_consumption;
	}

	




	public String getSpeed(){

		return this.speed;
	}

	public String getCo2Emission(){

		return this.co2_emission;
	}
	public String getFuelConsumption(){

		return this.fuel_consumption;
	}

	




	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof UnitSelection) {
			UnitSelection u = (UnitSelection) o;
			result = this.speed.equals(u.speed) 
					&& this.co2_emission.equals(u.co2_emission)
					&& this.fuel_consumption.equals(u.fuel_consumption);
					
		}
		return result;
	}





}

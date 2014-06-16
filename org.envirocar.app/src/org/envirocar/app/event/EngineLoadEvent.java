package org.envirocar.app.event;

public class EngineLoadEvent implements AbstractEvent<Double> {

	private double engineLoad;
	
	public EngineLoadEvent(double el) {
		engineLoad = el;
	}
	
	@Override
	public Double getPayload() {
		return engineLoad;
	}

}

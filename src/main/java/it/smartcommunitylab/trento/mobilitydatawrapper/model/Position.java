package it.smartcommunitylab.trento.mobilitydatawrapper.model;

import java.math.BigDecimal;

import com.vividsolutions.jts.geom.Point;

import it.smartcommunitylab.trento.mobilitydatawrapper.GeoUtils;

public class Position {

	private String place;
	private Double[] coordinates;

	public Position(Object[] dbEntry) {
		place = (String) dbEntry[0];
		double gx = ((BigDecimal)dbEntry[1]).doubleValue();
		double gy = ((BigDecimal)dbEntry[2]).doubleValue();
		
		Point point = GeoUtils.convert(gx, gy);
		
		coordinates = new Double[2];
		coordinates[0] = point.getX();
		coordinates[1] = point.getY();
		
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Double[] coordinates) {
		this.coordinates = coordinates;
	}

}

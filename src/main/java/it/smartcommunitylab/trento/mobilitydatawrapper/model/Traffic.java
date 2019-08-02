package it.smartcommunitylab.trento.mobilitydatawrapper.model;

import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Traffic implements Comparable<Traffic> {

	private Long time;
	private String place;
	private String station;
	private Integer value;

	public Traffic(Object[] dbEntry) {
		time = ((Timestamp)dbEntry[0]).getTime();
		place = readClob((Clob)dbEntry[1]);
		station = readClob((Clob)dbEntry[2]);
		value = (Integer) dbEntry[3];
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public int compareTo(Traffic o) {
		// TODO Auto-generated method stub
		return time.compareTo(o.time);
	}
	
	private String readClob(Clob clob) {
		try {
			return clob.getSubString(1, (int)clob.length());
		}catch(Exception e) {
			return null;
		} finally {
//			try {
//				clob.free();
//			} catch (SQLException e) {
//			}
		}
	}
}

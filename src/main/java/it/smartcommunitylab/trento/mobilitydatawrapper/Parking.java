package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.sql.Timestamp;

public class Parking {

	private String id;
	private String name;
	private Integer busy;
	private Integer free;
	private Long updated;

	public Parking(Object[] dbEntry) {
		name = (String) dbEntry[0];
		id = name.split(" ")[0];
		busy = (Integer) dbEntry[1];
		free = (Integer) dbEntry[2];
		updated = ((Timestamp)dbEntry[3]).getTime();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String nomeParcheggio) {
		this.name = nomeParcheggio;
	}

	public Integer getBusy() {
		return busy;
	}

	public void setBusy(Integer busy) {
		this.busy = busy;
	}

	public Integer getFree() {
		return free;
	}

	public void setFree(Integer free) {
		this.free = free;
	}

	public Long getUpdated() {
		return updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}


	
}

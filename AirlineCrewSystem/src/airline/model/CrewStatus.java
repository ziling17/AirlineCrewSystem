package airline.model;

import java.time.LocalDateTime;

public class CrewStatus {
    // ------------------------ PRIVATE ATTRIBUTES ------------------------
	private String crewId;
	private LocalDateTime start;
	private LocalDateTime end;
	private String status;
	private String flightId; //optional only for Scheduled status
	
	
    // ------------------------ CONSTRUCTOR ------------------------
	public CrewStatus (String crewId, LocalDateTime start, LocalDateTime end, String status, String flightId ){
		this.crewId = crewId;
		this.start = start;
		this.end =end;
		this.status = status;
		this.flightId = flightId;
	}
	
	
    // ------------------------ GETTER METHODS ------------------------
	public String getCrewId() {
		return crewId;	
	}
	
	public LocalDateTime getStart() {
		return start;
	}
	
	public LocalDateTime getEnd() {
		return end;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getFlightId() {
		return flightId;
	}
	

    // ------------------------ SETTER METHODS ------------------------
	public void setStatus(String status) {
		if(status != null && !status.isBlank())
			this.status = status;
	}
	

    // ------------------------ CHECK TIME OVERLAP ------------------------
	public boolean overlaps (LocalDateTime s, LocalDateTime e) {
		if(s==null|| e== null|| start==null|| end == null) {
			return false;
		}
		//  overlap if the start<=e && s<=end
		return (!this.start.isAfter(e)) && (!s.isAfter(this.end));
	}
	
	
    // ------------------------ TO STRING ------------------------
	public String toString() {
		return "CrewStatus{" + "crewId='" + crewId + '\'' + ", start=" + start + ", end=" + end + ", status='" + status + '\'' + ", flightId='" + flightId + '\'' + '}';
	}
}


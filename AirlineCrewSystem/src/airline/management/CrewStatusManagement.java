package airline.management;

import airline.database.Database;
import airline.model.Leave;
import airline.model.Crew;
import airline.model.CrewStatus;
import java.time.LocalDateTime;

public class CrewStatusManagement {
	
	private Database db;
	public static final double MAX_FLIGHT_HOURS = 55.0;
	
	//---------------------------- CONSTRUCTOR ----------------------------
	public CrewStatusManagement(Database db) {
		this.db =db;
	}
	
	
	//----------------------- ADD CREW STATUS RECORD -----------------------
	public void addStatus (String crewId, LocalDateTime start, LocalDateTime end, String status, String flightId) {
		
		// Validate input
		if(crewId == null|| crewId.isEmpty()|| start == null|| end == null|| status == null|| status.isEmpty()) {
			System.out.println("Invalid input. Crew status not added.");
			return;
		}
		
		// Add new Crew Status record to database
		db.getStatusList().add(new CrewStatus(crewId, start, end, status, flightId));
	}
		
	
	//--------------------- REMOVE STATUS BY FLIGHT ID ---------------------
	// Used when reassigning crew for a flight
	public void removeStatusByFlight (String flightId) { 
		if (flightId == null || flightId.isBlank()) {
			return;
		}
		
		// Remove all status records linked to the flight
		db.getStatusList().removeIf(cs-> flightId != null && flightId.equals(cs.getFlightId()));
	}
	
	
	//---------------- REMOVE CREW STATUS FOR SPECIFIC FLIGHT---------------- 
	// Used when removing a crew from a flight
	public void removeCrewStatusRecord (String crewId, String flightId) { 
		if (crewId == null || crewId.isBlank() || flightId == null || flightId.isBlank()){
			return;
		}
		
		// Remove matching crew + flight status
		db.getStatusList().removeIf(cs-> cs.getCrewId().equals(crewId) && flightId.equals(cs.getFlightId()));
	}
	
	
	//---------------------- REMOVE OLD LEAVE RECORDS -----------------------
	// Used when updating or cancel leave
	public void removeOldRecords (String crewId, LocalDateTime start, LocalDateTime end) {
		if (crewId == null || crewId.isBlank() || start == null || end == null) {
			return;
		}
		
		// Remove overlapping leave status records
		db.getStatusList().removeIf (cs->cs.getCrewId().equals(crewId) && cs.overlaps(start,end)&& "Leave".equalsIgnoreCase(cs.getStatus()));
	}
	
	
	//------------------- CHECK CREW AVAILABILITY STATUS -------------------
	// Returns: Leave / Scheduled / Exceed flight hours / Available
	public String getStatus(String crewId, LocalDateTime start, LocalDateTime end) {
	    
		// Check Approved Leave
	    for (Leave l : db.getLeaveList()) {
	        if (l.getCrewID().equalsIgnoreCase(crewId) && l.getStatus().equalsIgnoreCase("Approved")) {
	            LocalDateTime leaveStart = l.getStartDate().atStartOfDay();
	            LocalDateTime leaveEnd = l.getEndDate().atTime(23, 59);
	            
	            // Check overlap with requested period
	            if (!start.isAfter(leaveEnd) && !end.isBefore(leaveStart)) {
	                return "Leave";
	            }
	        }
	    }

	    // Check Scheduled flights
	    boolean hasScheduled = false;
	    
	    for (CrewStatus cs : db.getStatusList()) {
	        if (!cs.getCrewId().equalsIgnoreCase(crewId)) continue;					// Skip different crew
	        if (!cs.overlaps(start, end)) continue;									// Skip if no overlap
	        if (cs.getStatus().equalsIgnoreCase("Leave")) return "Leave";			// Leaves takes priority
	        if (cs.getStatus().equalsIgnoreCase("Scheduled")) hasScheduled = true;	// Mark scheduled status
	    }
	    if (hasScheduled) return "Scheduled";

	    // Check Maximum Flight Hours
	    Crew crew = db.getCrewList().stream().filter(c -> c.getId().equalsIgnoreCase(crewId)).findFirst().orElse(null);
	    if (crew != null && crew.getFlightHours() >= MAX_FLIGHT_HOURS) {
	        return "Exceed flight hours";
	    }

	    // If none of the above, crew is available
	    return "Available";
	}
}

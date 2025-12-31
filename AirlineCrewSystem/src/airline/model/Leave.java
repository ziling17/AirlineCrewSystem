package airline.model;

import java.time.LocalDate;
import airline.notification.Notifier;
import airline.notification.ConsoleNotifier;

public class Leave {

    // ------------------------ PRIVATE ATTRIBUTES ------------------------
	private String leaveID;
	private String crewID;
	private String reason;
	private LocalDate startDate;
	private LocalDate endDate;
	private String status;
    private Notifier notifier;
	
	
    // ------------------------ CONSTRUCTOR ------------------------
	public Leave(String leaveID, String crewID, String reason, LocalDate startDate, LocalDate endDate, String status) {
		this.leaveID = leaveID;
		this.crewID = crewID;
		this.reason = reason;
		this.startDate = startDate;
		this.endDate = endDate;
		setStatus(status);
        this.notifier = new ConsoleNotifier();
	}
	

    // ------------------------ GETTER METHODS ------------------------
    public String getLeaveID() { 
    	return leaveID; 
    }
    public String getCrewID() { 
    	return crewID; 
    }
    public String getReason() { 
    	return reason; 
    }
    public LocalDate getStartDate() { 
    	return startDate; 
    }
    public LocalDate getEndDate() { 
    	return endDate; 
    }
    public String getStatus() { 
    	return status; 
    }

    public String getCrewStatus (LocalDate date) {
    	if(date != null && startDate != null && endDate != null){
    		if(!date.isBefore(startDate) && !date.isAfter(endDate)) {
    			return "On Leave";
    		}
    	}
    	return "Available";
    }
    

    // ------------------------ SETTER METHODS ------------------------
    public void setCrewID(String crewID) {
        if (crewID != null && !crewID.isEmpty())
            this.crewID = crewID;
    }  
    public void setReason(String reason) {
        if (reason != null && !reason.isEmpty())
            this.reason = reason;
    }   
    public void setStartDate(LocalDate startDate) { 
        if(startDate != null && (endDate == null||!startDate.isAfter(endDate)))
            this.startDate = startDate; 
    }
    public void setEndDate(LocalDate endDate) { 
    	if(endDate != null && (startDate == null|| !endDate.isBefore(startDate)))
            this.endDate = endDate; 
    }

    public void setStatus(String status) { 
        if(status.equals("Pending") || status.equals("Approved") || status.equals("Rejected")) {
            this.status = status;
        }
    }
    
    
    // ------------------------ TO STRING ------------------------
    public String toString() {
        return "Leave{" + "leaveID='" + leaveID + '\'' + ", crewID='" + crewID + '\'' + ", reason='" + reason + '\'' +", startDate=" + startDate + ", endDate=" + endDate + ", status='" + status + '\'' +'}';
    }
    
    
    // --------------------- NOTIFICATION ----------------------
    public void sendNotification(String message) {
        notifier.notify(message);
    }

}
   

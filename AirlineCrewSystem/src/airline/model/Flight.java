package airline.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import airline.notification.Notifier;
import airline.notification.ConsoleNotifier;

public class Flight{
	
    // ------------------------ PRIVATE ATTRIBUTES ------------------------
	private String flightID;
    private String origin;
    private String destination;
    private Notifier notifier;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private ArrayList<String> crew;


    // ------------------------ CONSTRUCTOR ------------------------
    public Flight(String flightID, String origin, String destination, LocalDateTime departureDate, LocalDateTime arrivalDate) {
        this.flightID = flightID;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.crew = new ArrayList<>();
        this.notifier = new ConsoleNotifier();
    }


    // ------------------------ GETTER METHODS ------------------------
    public String getId() { 
    	return flightID; 
    }
    public String getOrigin() {
    	return origin; 
    }
    public String getDestination() { 
    	return destination; 
    }
    public LocalDateTime getDepartureDate() { 
    	return departureDate; 
    }
    public LocalDateTime getArrivalDate() { 
    	return arrivalDate; 
    }
    public ArrayList<String> getCrew() {
    	return new ArrayList<>(crew);
    }
    

    // ------------------------ SETTER METHODS ------------------------
    public void setOrigin(String origin) { 
        if(origin != null && !origin.isEmpty())
            this.origin = origin;
    }	
    public void setDestination(String destination) { 
        if(destination != null && !destination.isEmpty())
            this.destination = destination;
    }
    public void setDepartureDate(LocalDateTime departureDate) { 
    	if(departureDate != null )
            this.departureDate = departureDate;
    }
    public void setArrivalDate(LocalDateTime arrivalDate) { 
    	if(arrivalDate != null)
            this.arrivalDate = arrivalDate;
    }
    
    
    // ------------------------ CREW MANAGEMENT ------------------------
    public void addCrew(String crewID) {
    	if(crewID != null && !crewID.isEmpty() && !crew.contains (crewID)) 
    		crew.add(crewID);
    }
    
    public void removeCrew(String crewID) {
    		crew.remove(crewID);
    }
    
    
    // ------------------------ FLIGHT DURATION ------------------------
    public double calculateDurationHours() {
    	if (departureDate != null && arrivalDate != null) {
    		Duration duration = Duration.between(departureDate, arrivalDate);
    		return duration.toMinutes()/60.0; //duration in hours
    	}
    	return 0.0;
    }
    
    
    // ------------------------ CHECK OVERLAP ------------------------
    public boolean isOverlapping(Flight other) {
    	if(other == null|| departureDate == null|| arrivalDate == null) {
    		return false;
    	}
    	return !(arrivalDate.isBefore(other.getDepartureDate())|| departureDate.isAfter(other.getArrivalDate()));
    }
    
    
    // ------------------------ TO STRING ------------------------
    public String toString() {
    	return flightID;
    }
    
    
    // --------------------- NOTIFICATION ----------------------
    public void sendNotification(String message) {
        notifier.notify(message);
    }


}
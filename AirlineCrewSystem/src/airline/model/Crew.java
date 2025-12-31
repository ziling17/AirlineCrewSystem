package airline.model;

import java.util.ArrayList;
import airline.notification.Notifier;
import airline.notification.ConsoleNotifier;


public class Crew extends Person {

    // ------------------------ PRIVATE ATTRIBUTES ------------------------
    private String role;
    private double flightHours;
    private ArrayList<Flight> assignedFlights;
    private Notifier notifier;  // polymorphic notification channel

    // ------------------------ CONSTRUCTORS ------------------------
    // Default constructor uses console notifier
    public Crew(String crewID, String name, String role, String phone) {
        this(crewID, name, role, phone, new ConsoleNotifier());
    }

    // Constructor with custom notifier
    public Crew(String crewID, String name, String role, String phone, Notifier notifier) {
        super(crewID, name, phone);
        this.role = role;
        this.flightHours = 0.0;
        this.assignedFlights = new ArrayList<>();
        this.notifier = (notifier != null) ? notifier : new ConsoleNotifier();
    }

    
    // ------------------------ GETTER METHODS ------------------------
    public String getRole() { 
    	return role; 
    }
    public double getFlightHours() { 
    	return flightHours; 
    }
    public ArrayList<Flight> getAssignedFlights() { 
    	return assignedFlights; 
    }

    
    // ------------------------ SETTER METHODS ------------------------
    public void setRole(String role) { 
    	if (role != null && !role.isBlank()) 
    		this.role = role; 
    }
    
    public void setFlightHours(double hours) { 
    	if (hours >= 0) 
    		this.flightHours = hours; 
    }

    
    // ------------------------ FLIGHT ASSIGNMENT METHODS ------------------------
    public void addAssignedFlight(Flight flight) {
        if (flight != null && !assignedFlights.contains(flight)) {
            assignedFlights.add(flight);
            flightHours += flight.calculateDurationHours();
        }
    }

    public void removeAssignedFlight(Flight flight) {
        if (flight != null && assignedFlights.remove(flight))
            flightHours -= flight.calculateDurationHours();
    }

    
    // ------------------------ DISPLAY ASSIGNED FLIGHT IDS ------------------------
    public String getAssignedFlightIDs() {
        if (assignedFlights.isEmpty()) return "-";
        StringBuilder result = new StringBuilder();
        for (Flight f : assignedFlights) 
        	result.append(f.getId()).append(" ");
        return result.toString().trim();
    }

    
    // ------------------------ TO STRING ------------------------
    @Override
    public String toString() {
        return "Crew{" + "id=" + getId() + ", name=" + getName() + ", role=" + role +", phone=" + getPhone() + ", flightHours=" + flightHours + '}';
    }

    // --------------------- NOTIFICATION ----------------------
    public void sendNotification(String message) {
        notifier.notify(message);
    }

    // Allow switching notification channel at runtime
    public void setNotifier(Notifier notifier) {
        if (notifier != null) 
        	this.notifier = notifier;
    }
}

package airline.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import airline.model.Crew;
import airline.model.Flight;
import airline.model.Leave;
import airline.model.CrewStatus;

public class Database {
	
	//--------------------------- DATA STORAGE ----------------------------
	private ArrayList<Crew> crewList;			// Store all crew members
	private ArrayList<Flight> flightList;		// Stores all flights
	private ArrayList<Leave> leaveList;			// Stores all leaves applied by crew
	private ArrayList<CrewStatus> statusList;	// Stores crew daily status (Scheduled/Leave)
	
	//--------------------------- CONSTRUCTOR -----------------------------
	public Database() {
		crewList = new ArrayList<>();
		flightList = new ArrayList<>();
		leaveList = new ArrayList<>();
		statusList = new ArrayList<>();
	}
   
	//------------------------- LOAD SAMPLE DATA --------------------------
	public void loadSampleData() {

	    // CREW MEMBER
	    Crew[] c = {
	            new Crew("C001","Amir Rahman","Pilot","012-3456789"),
	            new Crew("C002","Nur Hidayah","Pilot","013-4567890"),
	            new Crew("C003","John Tan","Co-Pilot","014-5678901"),
	            new Crew("C004","Aisha Zulkifli","Cabin Crew","012-6789012"),
	            new Crew("C005","Samuel Lee","Pilot","013-7890123"),
	            new Crew("C006","Farah Nabila","Cabin Crew","014-8901234"),
	            new Crew("C007","Hafiz Abdullah","Co-Pilot","012-9012345"),
	            new Crew("C008","Chloe Lim","Cabin Crew","013-0123456"),
	            new Crew("C009","Daniel Wong","Pilot","014-1234567"),
	            new Crew("C010","Siti Amina","Cabin Crew","012-2345678"),
	            new Crew("C011","Kevin Raj","Co-Pilot","013-3456789"),
	            new Crew("C012","Sarah Tan","Cabin Crew","014-4567890"),
	            new Crew("C013","Lim Wei Xuan","Pilot","012-5678901"),
	            new Crew("C014","Maya Arif","Cabin Crew","013-6789012"),
	            new Crew("C015","Adam Firdaus","Co-Pilot","014-7890123"),
	            new Crew("C016","Grace Yong","Cabin Crew","012-8901234"),
	            new Crew("C017","Faizal Karim","Pilot","013-9012345"),
	            new Crew("C018","Lily Wong","Cabin Crew","014-0123456"),
	            new Crew("C019","Haziq Ibrahim","Co-Pilot","012-1234567"),
	            new Crew("C020","Nur Syafiqah","Cabin Crew","013-2345678"),
	            new Crew("C021","Irfan Hakim","Pilot","014-3456789"),
	            new Crew("C022","Alya Putri","Co-Pilot","012-4567890"),
	            new Crew("C023","Rina Hassan","Cabin Crew","013-5678901"),
	            new Crew("C024","Danish Omar","Cabin Crew","014-6789012"),
	            new Crew("C025","Sophia Tan","Cabin Crew","012-7890123"),
	            new Crew("C026","Alex Chong","Pilot","013-8901234"),
	            new Crew("C027","Bella Lim","Cabin Crew","014-9012345"),
	            new Crew("C028","Chris Tan","Co-Pilot","012-0123456"),
	            new Crew("C029","Diana Lee","Cabin Crew","013-1234567"),
	            new Crew("C030","Ethan Wong","Pilot","014-2345678"),
	            new Crew("C031","Fiona Ng","Cabin Crew","012-3456789"),
	            new Crew("C032","Gavin Low","Co-Pilot","013-4567890"),
	            new Crew("C033","Hannah Lim","Cabin Crew","014-5678901"),
	            new Crew("C034","Ian Teoh","Pilot","012-6789012"),
	            new Crew("C035","Jasmine Tan","Cabin Crew","013-7890123"),
	            new Crew("C036","Kyle Ong","Co-Pilot","014-8901234"),
	            new Crew("C037","Laura Wong","Cabin Crew","012-9012345"),
	            new Crew("C038","Mark Lee","Pilot","013-0123456"),
	            new Crew("C039","Nina Tan","Cabin Crew","014-1234567"),
	            new Crew("C040","Omar Rizal","Co-Pilot","012-2345678")
	    };

	    // Add all crew to the main list
	    for (Crew crew : c) crewList.add(crew);
	    
	    // FLIGHT HOUR SETUP
	    for (Crew crew : crewList) {
	    	switch (crew.getId()) {
	            case "C001": crew.setFlightHours(50.0); break;
	            case "C002": crew.setFlightHours(55.0); break;
	            case "C003": crew.setFlightHours(60.0); break;
	            case "C004": crew.setFlightHours(30.0); break;
	            case "C005": crew.setFlightHours(0.0); break;
	            case "C021": crew.setFlightHours(45.0); break;
	            case "C022": crew.setFlightHours(70.0); break;
	            case "C023": crew.setFlightHours(52.0); break;
	            case "C024": crew.setFlightHours(10.0); break;
	            case "C025": crew.setFlightHours(55.5); break;
	            default: crew.setFlightHours(25.0); break;
	        }
	    }

	    // FLIGHT
	    Flight[] f = {
	    	    new Flight("F001","KUL","SIN", LocalDateTime.of(2026,1,1,8,0),  LocalDateTime.of(2026,1,1,11,0)),
	    	    new Flight("F002","SIN","KUL", LocalDateTime.of(2026,1,1,14,0), LocalDateTime.of(2026,1,1,17,0)),
	    	    new Flight("F003","KUL","BKK", LocalDateTime.of(2026,1,2,9,0),  LocalDateTime.of(2026,1,2,11,30)),
	    	    new Flight("F004","BKK","KUL", LocalDateTime.of(2026,1,2,15,0), LocalDateTime.of(2026,1,2,17,30)),
	    	    new Flight("F005","KUL","HKG", LocalDateTime.of(2026,1,3,8,30), LocalDateTime.of(2026,1,3,12,0)),
	    	    new Flight("F006","HKG","KUL", LocalDateTime.of(2026,1,3,16,0), LocalDateTime.of(2026,1,3,19,30)),
	    	    new Flight("F007","KUL","NRT", LocalDateTime.of(2026,1,4,7,0),  LocalDateTime.of(2026,1,4,14,0)),
	    	    new Flight("F008","NRT","KUL", LocalDateTime.of(2026,1,4,17,0), LocalDateTime.of(2026,1,4,23,0)),
	    	    new Flight("F009","KUL","SYD", LocalDateTime.of(2026,1,5,9,0),  LocalDateTime.of(2026,1,5,18,0)),
	    	    new Flight("F010","SYD","KUL",LocalDateTime.of(2026,1,5,21,0), LocalDateTime.of(2026,1,6,6,0)),
	    	    new Flight("F011","KUL","MEL", LocalDateTime.of(2026,1,6,8,0), LocalDateTime.of(2026,1,6,17,0)),
	    	    new Flight("F012","MEL","KUL", LocalDateTime.of(2026,1,6,19,0), LocalDateTime.of(2026,1,7,4,0)),
	    	    new Flight("F013","KUL","SGN", LocalDateTime.of(2026,1,7,9,0), LocalDateTime.of(2026,1,7,12,0)),
	    	    new Flight("F014","SGN","KUL", LocalDateTime.of(2026,1,7,14,0), LocalDateTime.of(2026,1,7,17,0))
	    	};

	    // Add all flights to flight list
	    for (Flight flight : f) flightList.add(flight);
	   

	    // ASSIGN CREW TO FLIGHT
        assign(c[0], c[2], c[4], c[5], f[0]);
        assign(c[1], c[3], c[6], c[7], f[1]);
        assign(c[8], c[10], c[11], c[12], f[2]);
        assign(c[13], c[14], c[15], c[16], f[3]);
        assign(c[17], c[18], c[19], c[20], f[4]);
        assign(c[21], c[22], c[23], c[24], f[5]);
        assign(c[25], c[26], c[27], c[28], f[6]);
        assign(c[29], c[30], c[31], c[32], f[7]);
        assign(c[33], c[34], c[35], c[36], f[8]);
        assign(c[37], c[38], c[39], c[0], f[9]);
        assign(c[1], c[2], c[3], c[4], f[10]);
        assign(c[5], c[6], c[7], c[8], f[11]);
        assign(c[9], c[10], c[11], c[12], f[12]);
        assign(c[13], c[14], c[15], c[16], f[13]);
	    

	    // LEAVES
	    // Add leave application to leave list        
        leaveList.add(new Leave("L001","C003","Medical Leave", LocalDate.of(2026,1,2), LocalDate.of(2026,1,2),"Approved"));
        leaveList.add(new Leave("L002","C008","Family Matters", LocalDate.of(2026,1,4), LocalDate.of(2026,1,5),"Pending"));
        leaveList.add(new Leave("L003","C012","Annual Leave", LocalDate.of(2026,1,1), LocalDate.of(2026,1,2),"Approved"));
        leaveList.add(new Leave("L004","C017","Emergency", LocalDate.of(2026,1,3), LocalDate.of(2026,1,4),"Rejected"));
        leaveList.add(new Leave("L005","C022","Medical Leave", LocalDate.of(2026,1,5), LocalDate.of(2026,1,5),"Approved"));
        leaveList.add(new Leave("L006","C025","Annual Leave", LocalDate.of(2026,1,6), LocalDate.of(2026,1,6),"Approved"));
	
        // Convert approved leaves to crew status
        for (Leave leave : leaveList) {
	        if (!leave.getStatus().equalsIgnoreCase("Approved"))  continue;
	        LocalDateTime start = leave.getStartDate().atStartOfDay();
	        LocalDateTime end   = leave.getEndDate().atTime(23,59);
	        statusList.add(new CrewStatus(leave.getCrewID(), start,end, "Leave", null));
	    }
	}

	//----------------------- ASSIGN CREW TO FLIGHT -----------------------
	private void assign(Crew pilot, Crew copilot, Crew cabin1, Crew cabin2, Flight flight) {
	    
		// Add crew ID to flight
	    flight.addCrew(pilot.getId());
	    flight.addCrew(copilot.getId());
	    flight.addCrew(cabin1.getId());
	    flight.addCrew(cabin2.getId());

	    // Link flight to crew's assigned flights
	    pilot.addAssignedFlight(flight);
	    copilot.addAssignedFlight(flight);
	    cabin1.addAssignedFlight(flight);
	    cabin2.addAssignedFlight(flight);

	    // Add Crew Status (Scheduled) for the whole day
	    LocalDateTime dayStart = flight.getDepartureDate().toLocalDate().atStartOfDay();
	    LocalDateTime dayEnd   = flight.getDepartureDate().toLocalDate().atTime(23,59);

	    statusList.add(new CrewStatus(pilot.getId(), dayStart, dayEnd, "Scheduled", flight.getId()));
	    statusList.add(new CrewStatus(copilot.getId(), dayStart, dayEnd, "Scheduled", flight.getId()));
	    statusList.add(new CrewStatus(cabin1.getId(), dayStart, dayEnd, "Scheduled", flight.getId()));
	    statusList.add(new CrewStatus(cabin2.getId(), dayStart, dayEnd, "Scheduled", flight.getId()));
	}

	
	//----------------------------- GETTERS -------------------------------
    public ArrayList<Crew> getCrewList() {
        return crewList;
    }

    public ArrayList<Flight> getFlightList() {
        return flightList;
    }

    public ArrayList<Leave> getLeaveList() {
        return leaveList;
    }
    
    public ArrayList<CrewStatus> getStatusList(){
    	return statusList;
    }  
}

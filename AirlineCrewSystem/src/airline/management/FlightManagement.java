package airline.management;

import airline.database.Database;
import airline.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Scanner;
import java.util.ArrayList;
import airline.notification.Notifier;
import airline.notification.ConsoleNotifier;
import airline.notification.EmailNotifier;

public class FlightManagement {
		
	private Database db;
	private CrewStatusManagement manageStatus;
	private static final double MAX_FLIGHT_HOURS = 55.0; //max monthly flight hours
	private Scanner in = new Scanner(System.in);
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Notifier emailNotifier = new EmailNotifier();
    private Notifier consoleNotifier = new ConsoleNotifier();
	
	//---------------------------- CONSTRUCTOR ----------------------------
	public FlightManagement(Database db) {
		this.db =db;
		this.manageStatus = new CrewStatusManagement(db);
	}
		
	
	//------------------------ FLIGHT MANAGEMENT MAIN MENU ------------------------
	public void viewFlightMenu() {		
		int choice = 0;	
		do {
			System.out.println("\n\n =========================================");
			System.out.println(" |    FLIGHT SCHEDULE MANAGEMENT MENU    |");
			System.out.println(" =========================================");
			System.out.println(" |  1. View All Schedules                |");
			System.out.println(" |  2. Add New Schedule                  |");
			System.out.println(" |  3. Assign Crew to Flight             |");
			System.out.println(" |  4. Update Schedule                   |");
			System.out.println(" |  5. Delete Schedule                   |");
			System.out.println(" |  6. Search Schedule                   |");
			System.out.println(" |  7. Back To Main Menu                 |");
			System.out.println(" =========================================");		
			System.out.print(" Enter your choice (1-7): ");
			
			//Input Validation (integer)
			if (in.hasNextInt()) {
                choice = in.nextInt();
                in.nextLine();
            } else {
                System.out.println("\n Invalid input! Please enter a number 1-7.");
                in.nextLine();
                continue;
            }
			
			switch (choice) {
				case 1: 
					viewAllFlight(true);
					break;
				case 2: 
					addFlight();
					break;
				case 3:
					assignCrew();
					break;
				case 4: 
					updateFlight();
					break;
				case 5:
					deleteFlight();
					break;
				case 6:
					searchFlight();
					break; 
				case 7:
					System.out.println("\n Returning to Main Menu...");
					break;
				default: 
					System.out.println("\n Invalid choice. Please reenter your choice (1-7).");
			}
		} while(choice != 7);
	}
	
	
	//------------------------ VIEW ALL FLIGHTS ------------------------
	private void viewAllFlight(boolean askSort) {
	    int choice = 1;  // Default sort by Flight ID

	    // Ask for sorting option
	    if (askSort) {
	        System.out.println("\n ------ View Flight List ------");
	        System.out.println("\n 1. Sort by Flight ID");
	        System.out.println(" 2. Sort by Date & Time");
	        System.out.print("\n Enter choice (1/2): ");
	        try {
	            choice = Integer.parseInt(in.nextLine().trim());
	        } catch (NumberFormatException e) {
	            System.out.println("\n Invalid input. Showing default sorted list (Flight ID).");
	            choice = 1;  	// Default sorting
	        }
	    }

	    // Copy flight list form database for display
	    ArrayList<Flight> viewList = new ArrayList<>(db.getFlightList());

	    // Sort flight list based on user input
	    if (choice == 1) {
	        // Sort by Flight ID
	        viewList.sort(Comparator.comparing(Flight::getId, String.CASE_INSENSITIVE_ORDER));
	    } else {
	        // Sort by departure date & then arrival date
	        viewList.sort(Comparator.comparing(Flight::getDepartureDate)
	                                .thenComparing(Flight::getArrivalDate));
	    }

	    // Display flight table
	    System.out.println("\n\n------------------------------------------------- Flight List --------------------------------------------------");
	    System.out.println(" ===============================================================================================================");
	    System.out.printf(" | %-8s | %-6s | %-11s | %-16s | %-16s | %-35s |%n", "FlightID", "Origin", "Destination", "    Departure", "     Arrival", "          Assigned Crew");
	    System.out.println(" ===============================================================================================================");

	    // Display each part in table format
	    for (Flight f : viewList) {
	        String dep = f.getDepartureDate().format(fmt);
	        String arr = f.getArrivalDate().format(fmt);
	        System.out.printf(" |   %-6s |  %-5s |     %-7s | %-16s | %-16s | %-35s |%n", f.getId(), f.getOrigin(), f.getDestination(), dep, arr, f.getCrew());
	    }

	    System.out.println(" ===============================================================================================================");
	    System.out.println(" Total flight: " + viewList.size());
	}

	
	//------------------------ ADD NEW FLIGHT ------------------------
	public void addFlight() {    
		
	    System.out.println("\n\n--------------- Add New Flight Record ---------------");
	    String flightID = generateNextFlightID();
	    System.out.println("\n New Flight ID generated: " + flightID);

	    System.out.print(" Enter Origin (Eg.KUL): ");
	    String dep = in.nextLine().trim().toUpperCase();

	    System.out.print(" Enter Destination (Eg.HKG): ");
	    String arr = in.nextLine().trim().toUpperCase();

	    LocalDateTime depDate = readDateTime(" Enter Departure Date & Time (YYYYMMDD HHMM or YYYY-MM-DD HH:MM): ");
	    LocalDateTime arrDate = readDateTime(" Enter Arrival Date & Time (YYYYMMDD HHMM or YYYY-MM-DD HH:MM): ");

	    // Create new flight object
	    Flight newFlight = new Flight(flightID, dep, arr, depDate, arrDate);
	    
	    // Add flight to database
	    db.getFlightList().add(newFlight);
	    
	    consoleNotifier.notify("\n New flight added: " + flightID + " (" + dep + " → " + arr + ") Departure: " + depDate + " Arrival: " + arrDate);
	    System.out.println("\n\n-----------------------------------------------------");
	}

	
	//------------------------ READ DATE & TIME INPUT ------------------------
	private LocalDateTime readDateTime(String prompt) {
	    LocalDateTime dateTime = null;

	    // Keep prompting until a valid date-time is entered
	    while (dateTime == null) {
	        System.out.print(prompt);
	        String input = in.nextLine().trim().replaceAll("\\s+", "");

	        // Convert YYYYMMDDHHMM format to standard YYYY-MM-DD HH:MM
	        if (input.matches("\\d{12}")) { 
	            input = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8) + " " + input.substring(8, 10) + ":" + input.substring(10, 12);
	        }

	        try {
	            dateTime = LocalDateTime.parse(input, fmt);
	        } catch (DateTimeParseException e) {
	            System.out.println(" Invalid format! Please enter as YYYYMMDDHHMM or YYYY-MM-DD HH:MM\n");
	        }
	    }
	    return dateTime;
	}
	
	
	//------------------------ AUTO-GENERATE FLIGHT ID ------------------------
	private String generateNextFlightID() {
		int max = 0;
		
        for (Flight f : db.getFlightList()) {
            String id = f.getId();

            if (id.startsWith("F")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        int next = max + 1;
        return "F" + String.format("%03d", next);
    }

	
	//------------------------ ASSIGN CREW TO FLIGHT ------------------------
	public void assignCrew() {
		
	    viewAllFlight(false);
	    
	    System.out.println("\n\n-------------------- Assign Crew To Flight --------------------------");
	    System.out.print(" Enter Flight ID: ");
	    String flightID = in.nextLine().trim();

	    // Find flight by ID
	    Flight flight = db.getFlightList().stream().filter(f -> f.getId().equalsIgnoreCase(flightID)).findFirst().orElse(null);

	    if (flight == null) {
	        System.out.println("\n Flight not found. Invalid Flight ID!");
	        return;		// Exit if flight not found
	    }

	    // Display selected flight info
	    System.out.printf("%n%n Flight found: %s -> %s (%.2f hrs)%n",flight.getOrigin(), flight.getDestination(), flight.calculateDurationHours());
	    System.out.println(" Departure: " + flight.getDepartureDate());
	    System.out.println(" Arrival: " + flight.getArrivalDate());

	    // Display crew status list
	    System.out.println("\n Crew Status List: ");
	    System.out.println(" ===========================================================================================================");
	    System.out.printf(" | %-6s | %-20s | %-14s | %-15s |  %-35s |\n", "CrewID", "Name", "Role", "Flight Hours", "                Status");
	    System.out.println(" ===========================================================================================================");

	    // Temporary list of crew
	    ArrayList<Crew> crewList = new ArrayList<>(db.getCrewList());

	    // Sort crew based on status (Available first, then others alphabetically)
	    crewList.sort((c1, c2) -> {
	        String status1 = manageStatus.getStatus(c1.getId(), flight.getDepartureDate(), flight.getArrivalDate());
	        String status2 = manageStatus.getStatus(c2.getId(), flight.getDepartureDate(), flight.getArrivalDate());

	        if (status1.equalsIgnoreCase("Available") && !status2.equalsIgnoreCase("Available")) return -1;
	        if (!status1.equalsIgnoreCase("Available") && status2.equalsIgnoreCase("Available")) return 1;
	        return status1.compareTo(status2); // Other statuses alphabetically
	    });

	    // Display crew list with flight assignment info
	    for (Crew c : crewList) {
	        String status = manageStatus.getStatus(c.getId(), flight.getDepartureDate(), flight.getArrivalDate());

	        // Append assigned flight IDs if crew is scheduled
	        if (status.equalsIgnoreCase("Scheduled")) {
	            ArrayList<String> assignedFlightIDs = new ArrayList<>();
	            for (Flight f : c.getAssignedFlights()) {
	                assignedFlightIDs.add(f.getId());
	            }
	            if (!assignedFlightIDs.isEmpty()) {
	                status += " [" + String.join(",", assignedFlightIDs) + "]";
	            }
	        }

	        // Print crew details
	        System.out.printf(" |  %-5s | %-20s | %-14s |    %-12.1f |  %-35s |\n", c.getId(), c.getName(), c.getRole(), c.getFlightHours(), status);
	    }


	    System.out.println(" ===========================================================================================================");
	    System.out.println(" Current Assigned Crew ID: " + flight.getCrew());

	    // Handle flight with already assigned crew
	    if (flight.getCrew() != null && !flight.getCrew().isEmpty()) {
	        System.out.println(" Please choose an option: ");
	        System.out.println(" 1. Reassign crew");
	        System.out.println(" 2. Replace crew");
	        System.out.println(" 3. Add crew");
	        System.out.println(" 4. Not assign crew");
	        int option = 0;

	        while (true) {
	            System.out.print(" Enter your choice (1-4): ");
	            try {
	                option = Integer.parseInt(in.nextLine().trim());
	            } catch (NumberFormatException e) {
	                System.out.println("\n Invalid input! Please enter a number 1-4.");
	                continue;
	            }

	            switch (option) {
	                case 1: // Reassign all
	                    for (String oldCrewId : flight.getCrew()) {
	                        Crew oldCrew = db.getCrewList().stream()
	                                         .filter(c -> c.getId().equalsIgnoreCase(oldCrewId))
	                                         .findFirst().orElse(null);
	                        if (oldCrew != null) oldCrew.removeAssignedFlight(flight);
	                    }
	                    manageStatus.removeStatusByFlight(flight.getId());
	                    flight.getCrew().clear();
	                    break;

	                case 2: // Replace crew
	                    System.out.print("\n Enter Crew ID to replace: ");
	                    String replaceId = in.nextLine().toUpperCase().trim();
	                    if (flight.getCrew().contains(replaceId)) {
	                        flight.getCrew().remove(replaceId);
	                        manageStatus.removeCrewStatusRecord(replaceId, flight.getId());
	                        Crew oldCrew = db.getCrewList().stream()
	                                         .filter(c -> c.getId().equalsIgnoreCase(replaceId))
	                                         .findFirst().orElse(null);
	                        if (oldCrew != null) oldCrew.removeAssignedFlight(flight);
	                    } else {
	                        System.out.println("\n Crew ID not assigned to this flight.");
	                    }
	                    break;

	                case 3: break; // Add new crew

	                case 4:
	                    System.out.println("\n System return to Flight Management Menu.");
	                    return;

	                default:
	                    System.out.println("\n Invalid option! Please enter (1-4).");
	                    continue;
	            }
	            break;
	        }
	    }

	    // Input crew ID(s) to assign
	    System.out.print("\n Enter Crew ID to assign (comma-separated for multiple): ");
	    String crewInput = in.nextLine().toUpperCase().trim();
	    String[] crewIDs = crewInput.split(",");
	    System.out.println("\n -------------------------------------------------------");
	    System.out.println(" System checking crew availability...\n");

	    // Check each crew member for availability and assign
	    for (String id : crewIDs) {
	        String crewId = id.trim();
	        Crew crew = db.getCrewList().stream().filter(c -> c.getId().equalsIgnoreCase(crewId)).findFirst().orElse(null);

	        if (crew == null) {
	            System.out.println("\n Crew ID " + crewId + " (Skipped - ID Not found)");
	            continue;
	        }

	        // Check crew availability (Available / Leave / Scheduled)
	        String status = manageStatus.getStatus(crewId, flight.getDepartureDate(), flight.getArrivalDate());
	        if (!status.equalsIgnoreCase("Available")) {
	            if (status.equalsIgnoreCase("Scheduled")) {
	                ArrayList<String> overlappingFlights = new ArrayList<>();
	                for (Flight f : crew.getAssignedFlights()) {
	                    if (!f.getId().equalsIgnoreCase(flight.getId()) &&
	                        !(f.getArrivalDate().isBefore(flight.getDepartureDate()) || f.getDepartureDate().isAfter(flight.getArrivalDate()))) {
	                        overlappingFlights.add(f.getId());
	                    }
	                }
	                if (!overlappingFlights.isEmpty()) {
	                    status += " [" + String.join(",", overlappingFlights) + "]";
	                }
	            }
	            System.out.println("\n Crew " + crewId + " (Skipped - " + status + ")");
	            continue;
	        }

	        // Check if assigning would exceed max flight hours
	        double flightHours = flight.calculateDurationHours();
	        if (crew.getFlightHours() + flightHours > MAX_FLIGHT_HOURS) {
	            System.out.println("\n Crew " + crewId + " (Skipped - Will exceed flight hours)");
	            manageStatus.addStatus(crewId, flight.getDepartureDate(), flight.getArrivalDate(), "Exceed flight hours", null);
	            continue;
	        }

	        // Assign crew to flight
	        flight.addCrew(crewId);
	        crew.addAssignedFlight(flight);
	        manageStatus.addStatus(crewId, flight.getDepartureDate(), flight.getArrivalDate(), "Scheduled", flight.getId());
	        System.out.println("\n Crew " + crewId + " (OK)");
	        
	        emailNotifier.notify("Hello " + crew.getName() + ", " + "You have been assigned to flight " + flight.getId() + " from " + flight.getOrigin() + " to " + flight.getDestination() + " departing at " + flight.getDepartureDate() + ".");

	    }

	    // Display assignment summary
	    System.out.println("\n -------------------------------------------------------");
	    System.out.println(" Assigned Crew Summary for Flight (" + flight.getId() + "): \n");

	    if (flight.getCrew().isEmpty()) {
	        System.out.println(" No crew has been assigned to this flight");
	        return;
	    }

	    for (String id : flight.getCrew()) {
	        Crew crew = db.getCrewList().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
	        if (crew != null) {
	            System.out.println(" [" + crew.getId() + "] " + crew.getName() + " -> " + crew.getRole());
	        }
	    }

	    System.out.println("------------------------------------------------------");
	}
	



	//------------------------ FIND FLIGHT RECORDS ------------------------
	public Flight findFlight(String flightID) {
		for(Flight f : db.getFlightList()) {
			if(f.getId().equalsIgnoreCase(flightID)) {
				// Display flight details in table
				System.out.println("\n Search Result:");
				System.out.println(" ============================================================");
				System.out.printf(" | %-18s| %-36s |%n", "FlightID", f.getId());
				System.out.printf(" | %-18s| %-36s |%n", "Origin", f.getOrigin());
				System.out.printf(" | %-18s| %-36s |%n", "Destination", f.getDestination());
				System.out.printf(" | %-18s| %-36s |%n", "Departure", f.getDepartureDate());
				System.out.printf(" | %-18s| %-36s |%n", "Arrival", f.getArrivalDate());
				System.out.printf(" | %-18s| %-36s |%n", "Assigned Crew", f.getCrew());
				System.out.println(" ============================================================");
				return f;
			}
		}
		System.out.println(" Flight record not found !");
		return null;
	}
	
	//------------------------ UPDATE EXISTING FLIGHT ------------------------
	public void updateFlight() {
		
	    viewAllFlight(false);
	    
	    System.out.println("\n\n--------------------- Update Existing Flight Record --------------");
	    System.out.print(" Enter Flight ID: ");
	    String searchID = in.nextLine().toUpperCase().trim();

	    // Find flight record in database
	    Flight record = findFlight(searchID);
	    if(record == null) {
	        System.out.println(" Flight ID not found. Update cancelled. ");
	        return;
	    }

	    // Update flight origin
	    System.out.print(" Update Origin [Press Enter to keep current]: ");
	    String newOrigin = in.nextLine().trim();
	    if(!newOrigin.isBlank()) {
	        record.setOrigin(newOrigin.toUpperCase());
	    }

	    // Update flight destination
	    System.out.print(" Update Destination [Press Enter to keep current]: ");
	    String newDestination = in.nextLine().trim();
	    if(!newDestination.isBlank()) {
	        record.setDestination(newDestination.toUpperCase());
	    }

	    // Update departure and arrival date-time using flexible input
	    LocalDateTime newDepartureDate = readDateTime(" Update Departure Date & Time (YYYYMMDD HHMM or YYYY-MM-DD HH:MM) [Press Enter to keep current]: ", record.getDepartureDate());
	    LocalDateTime newArrivalDate   = readDateTime(" Update Arrival Date & Time (YYYYMMDD HHMM or YYYY-MM-DD HH:MM) [Press Enter to keep current]: ", record.getArrivalDate());

	    // Validate date-time inputs
	    if (newDepartureDate != null && newArrivalDate != null) {
	        if (newDepartureDate.isAfter(newArrivalDate)) {
	            System.out.println("\n Invalid flight date. Departure and Arrival not updated.");
	        } else {
	            record.setDepartureDate(newDepartureDate);
	            record.setArrivalDate(newArrivalDate);
	        }
	    } else if (newDepartureDate != null) {
	        if (newDepartureDate.isAfter(record.getArrivalDate())) {
	            System.out.println("\n Invalid Departure! Departure not updated.");
	        } else {
	            record.setDepartureDate(newDepartureDate);
	        }
	    } else if (newArrivalDate != null) {
	        if (newArrivalDate.isBefore(record.getDepartureDate())) {
	            System.out.println("\n Invalid Arrival! Arrival not updated.");
	        } else {
	            record.setArrivalDate(newArrivalDate);
	        }
	    }
	    
	    System.out.println("\n Flight record updated successfully!");
	    System.out.println(" (Crew assignment remains unchanged. To reassign crew, use the Assign Crew option.)\n");
	    
	    
	    // ------------------- Notify assigned crew about flight update -------------------
	    for (String crewId : record.getCrew()) {
	        Crew crew = db.getCrewList().stream().filter(c -> c.getId().equalsIgnoreCase(crewId)).findFirst().orElse(null);
	        if (crew != null) {
	            String msg = "Hello " + crew.getName() + ", Flight " + record.getId() + " has been updated. Please check the new schedule.";
	            emailNotifier.notify(msg);
	        }
	    }

	    System.out.println(" -----------------------------------------------------------------------------------------------------");
	}

	
	//------------------------ FLEXIBLE DATE-TIME INPUT ------------------------
	private LocalDateTime readDateTime(String prompt, LocalDateTime defaultValue) {
	    while (true) {
	        System.out.print(prompt);
	        String input = in.nextLine().trim().replaceAll("\\s+", "");

	        if (input.isBlank()) return null; // Keep current if blank

	        // Convert YYYYMMDDHHMM to YYYY-MM-DD HH:MM
	        if (input.matches("\\d{12}")) { 
	            input = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8) + " "
	                    + input.substring(8, 10) + ":" + input.substring(10, 12);
	        }

	        try {
	            return LocalDateTime.parse(input, fmt);
	        } catch (DateTimeParseException e) {
	            System.out.println(" Invalid format! Please enter as YYYYMMDD HHMM or YYYY-MM-DD HH:MM\n");
	        }
	    }
	}

	
	//------------------------ DELETE FLIGHT RECORD ------------------------
	public void deleteFlight() {
		viewAllFlight(false);
    	System.out.println("\n\n ----------------------- Delete Flight Records -----------------------");
    	System.out.print(" Enter Flight ID: ");
        String searchID = in.nextLine().trim();

        Flight record = findFlight(searchID);
        if (record == null){
        	System.out.println("\n Flight ID not found.");
            return;
        }
        
        // Remove flight from assigned crew lists
        if(record.getCrew()!=null) {
			for (String oldCrewId : record.getCrew()) {
				Crew oldCrew = db.getCrewList().stream().filter(c->c.getId().equals(oldCrewId)).findFirst().orElse(null);
				if(oldCrew!=null) {
					oldCrew.removeAssignedFlight(record);
				}
			}
			manageStatus.removeStatusByFlight (record.getId());
			record.getCrew().clear();
		}
    	
        // Confirm deletion
        System.out.print(" Are you sure you want to delete this record? (Y/N): ");
        String confirm = in.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            db.getFlightList().remove(record);
            System.out.println("\n Record deleted successfully.");
        } else {
        	System.out.println("\n Delete operation cancelled.");
        }
	}
	
	//------------------------ SEARCH FLIGHT ------------------------
	public void searchFlight() {
    	System.out.println("\n\n ----------------------- Search Flight Records -----------------------");
		System.out.print(" Enter Flight ID: ");
		String searchID = in.nextLine().trim();
		findFlight(searchID);
	}
}

		
		
package airline.management;

import java.util.Scanner;
import java.util.ArrayList;
import airline.database.Database;
import airline.model.Crew;
import airline.model.Flight;
import airline.notification.Notifier;
import airline.notification.ConsoleNotifier;
import airline.notification.EmailNotifier;


public class CrewManagement {
	
	private Database db;
	private CrewStatusManagement manageStatus;
    private Scanner in = new Scanner(System.in);
    private Notifier emailNotifier = new EmailNotifier();
    private Notifier consoleNotifier = new ConsoleNotifier();
    
	//---------------------------- CONSTRUCTOR ----------------------------
    public CrewManagement(Database db) {
    	this.db = db;
    	this.manageStatus = new CrewStatusManagement(db);
    }
    
    
	//--------------------- CREW MANAGEMENT MAIN MENU ----------------------
    public void viewCrewMenu() {
    	
    	int choice = 0;
    	
    	do {
			System.out.println("\n\n =================================");
			System.out.println(" |      CREW MANAGEMENT MENU     |");
			System.out.println(" =================================");
			System.out.println(" |  1. View All Crew             |");
			System.out.println(" |  2. Add New Crew              |");
			System.out.println(" |  3. Update Crew               |");
			System.out.println(" |  4. Delete Crew               |");
			System.out.println(" |  5. Back to Main Menu         |");
			System.out.println(" =================================");
			System.out.print(" Enter your choice: ");
			
            // Validate numeric input
			if (in.hasNextInt()) {
                choice = in.nextInt();
                in.nextLine();
            } else {
                System.out.println("\n Invalid input ! Enter a number 1-5.");
                in.nextLine();
                continue;
            }
			
			switch (choice) {
            case 1:
                viewAllCrew(true);
                break;
                
            case 2:
                addCrew();
                break;
                
            case 3:
                updateCrew();
                break;
                
            case 4:
                deleteCrew();
                break;
                
            case 5:
                System.out.println("\n Returning to Main Menu...");
                break;
                
            default:
                System.out.println("\n Invalid choice. Enter 1-5.");
                
			}
			
    	} while (choice != 5);
    }
    
    
	//---------------------------- VIEW ALL CREW----------------------------
    private void viewAllCrew(boolean askSort) {
        
    	int sortChoice = 1;

    	// Ask user for sorting option
        if (askSort) {
            System.out.println("\n ------------- View Crew List -------------");
            System.out.println(" 1. Sort by Crew ID");
            System.out.println(" 2. Sort By Role");
            System.out.print(" Enter choice (1/2): ");

            if (in.hasNextInt()) {
                sortChoice = in.nextInt();
                in.nextLine();
            } else {
                in.nextLine();
            }
        }

        // Copy crew list to avoid modifying original list
        ArrayList<Crew> crewCopy = new ArrayList<>(db.getCrewList());

        // Sort based on user selection
        if (sortChoice == 2) {
            crewCopy.sort((a, b) -> Integer.compare(roleOrder(a.getRole()), roleOrder(b.getRole())));
        } else {
            crewCopy.sort((a, b) -> a.getId().compareToIgnoreCase(b.getId()));
        }

        // Display crew table
        System.out.println("\n---------------------------------------------- Crew List ----------------------------------------------");
        System.out.println(" =======================================================================================================");
        System.out.printf(" | %-5s | %-19s | %-14s | %-14s |%-14s |%-21s|%n", "  ID", "        Name", "      Role", " Phone Number", " Flight Hours ", "  Assigned Flights");
        System.out.println(" =======================================================================================================");
        
        for (Crew c : crewCopy) {
            System.out.printf(" | %-5s | %-19s | %-14s |  %-13s |     %-9.1f | %-20s|%n", c.getId(), c.getName(), c.getRole(), c.getPhone(), c.getFlightHours(), c.getAssignedFlightIDs());
        }
        
        System.out.println(" =======================================================================================================");
        System.out.println(" Total crew: " + crewCopy.size());
    }


	//------------------- DEFINE SORTING PRORITY BY ROLE -------------------
    private int roleOrder(String role) {
        return switch (role.toLowerCase()) {
            case "pilot" -> 1;
            case "co-pilot" -> 2;
            case "cabin crew" -> 3;
            default -> 4;
        };
    }

	    
	//---------------------------- ADD NEW CREW ----------------------------
    private void addCrew() {
    	
    	System.out.println("\n\n ------------------ Add New Crew Record ------------------");
    	
    	// Auto-generate crew ID
    	String crewID = generateNextCrewID();
    	System.out.println(" New Crew ID generated: " + crewID);
        
    	// Get user input
    	System.out.print(" Enter Crew Name: ");
        String name = in.nextLine();
        
        System.out.print(" Enter Role [Pilot / Co-Pilot / Cabin Crew]: ");
        String role = inputValidRole();
        
        System.out.print(" Enter Phone Number: ");
        String phone = in.nextLine().replaceAll("[^0-9-]", "");
        if (phone.length() > 12) phone = phone.substring(0, 12);

        // Save new crew into database
        Crew newCrew = new Crew(crewID, name, role, phone);     
        db.getCrewList().add(newCrew);
        
        // Send console notification
        newCrew.sendNotification("\n New crew added: " + newCrew.getName() + " (ID: " + newCrew.getId() + ")");

        // Send email notification to the crew
        newCrew.setNotifier(emailNotifier); // switch to email notifier
        newCrew.sendNotification(" Welcome " + newCrew.getName() + "! You have been added as " + newCrew.getRole() + ".");
    }
	    
    
	//--------------------- AUTO-GENERATE NEXT CREW ID ---------------------
	private String generateNextCrewID() {
	    int max = 0;
	
	    // Find largest existing crew number
	    for (Crew c : db.getCrewList()) {
	        String id = c.getId();
	
	        if (id.startsWith("C")) {
	             try {
	               int num = Integer.parseInt(id.substring(1));
	               if (num > max) max = num;
	             } catch (NumberFormatException e) {
	            	 // Ignore invalid ID
	             }
	       }
	    }
	    
	    //Generate next ID
	    int next = max + 1;
	    return "C" + String.format("%03d", next);
	}

	
	//----------------------- SEARCH CREW BY CREW ID -----------------------
    public Crew findCrew(String crewID) {
        for (Crew c : db.getCrewList()) {
            if (c.getId().equalsIgnoreCase(crewID)) {
                
            	// Display crew details
                System.out.println("\n Search Result:");
                System.out.println(" =====================================");
                System.out.printf(" |%-19s| %-12s  |%n", " Crew ID", c.getId());
                System.out.printf(" |%-19s| %-12s  |%n", " Name", c.getName());
                System.out.printf(" |%-19s| %-12s  |%n", " Role", c.getRole());
                System.out.printf(" |%-19s| %-12s  |%n", " Phone", c.getPhone());
                System.out.printf(" |%-19s| %-12.1f  |%n", " Flight Hours", c.getFlightHours());
                System.out.printf(" |%-19s| %-12s  |%n", " Assigned Flight",c.getAssignedFlightIDs());
                System.out.println(" =====================================");
                
                return c;
            }
        }
        
        System.out.println("Crew record not found!");
        return null;
    }

	    
	//------------------------ UPDATE EXISTING CREW ------------------------
    private void updateCrew() {
    	
        viewAllCrew(false);
        
        System.out.println("\n\n --------------------------- Update Existing Crew Record ---------------------------");
        System.out.print("Enter Crew ID: ");
        String searchID = in.nextLine().trim().toUpperCase();

        Crew record = findCrew(searchID);
        if (record == null) return;
        
        boolean updated = false;

        // Update name
        System.out.print(" Enter new Crew Name [Press Enter to keep current]: ");
        String name = in.nextLine().trim();
        if (!name.isBlank()) {
        	record.setName(name);
        	updated = true;
        }


        // Update role
        System.out.print(" Enter new Role [Press Enter to keep current]: ");
        String role = in.nextLine().trim();
        if (!role.isBlank()) {
            role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
            record.setRole(role);
            updated = true;
        }

        // Update role
        System.out.print(" Enter new Phone Number [Press Enter to keep current]: ");
        String phone = in.nextLine().replaceAll("[^0-9-]", "");
        if (phone.length() > 12) phone = phone.substring(0, 12);
        if (!phone.isBlank()) {
        	record.setPhone(phone);
        	updated = true;
        }
        
        if (updated) {
        	record.setNotifier(consoleNotifier);
            record.sendNotification(" Crew record updated successfully (Crew ID: " + record.getId() + ")");
        } else {
            System.out.println("\n No changes made. Crew record remains unchanged.");
        }
    }

    
	//------------------------- DELETE CREW RECORD -------------------------
    private void deleteCrew() {
    	
    	viewAllCrew(false);
    	
    	System.out.println("\n\n ------------------- Delete Crew Record ------------------");
       	System.out.print(" Enter Crew ID: ");
        String searchID = in.nextLine().trim();

        Crew record = findCrew(searchID);
        if (record == null){
        	System.out.println("\n Crew ID not found !");
            return;
        }
        
        // Confirm deletion
        System.out.print(" Are you sure you want to delete this record? (Y/N): ");
        String confirm = in.nextLine().toUpperCase();
        
        if (confirm.equals("Y")) {
        	
        	// Remove crew from assigned flights and status records
        	for (Flight flight: record.getAssignedFlights()) {
            	flight.removeCrew(searchID);
            	manageStatus.removeCrewStatusRecord(searchID, flight.getId());
            }
        	
        	record.getAssignedFlights().clear();
            db.getCrewList().remove(record);
            

            record.setNotifier(consoleNotifier);
            record.sendNotification(" Crew record deleted: " + record.getName() + " (ID: " + record.getId() + ")");
        } else {
        	System.out.println("\n Delete operation cancelled !");
        }
        
    }
    
    
	//------------------------ VALIDATE ROLE INPUT -------------------------
    private String inputValidRole() {
        while (true) {
            String input = in.nextLine().trim().toLowerCase();

            switch (input) {
                case "pilot":
                    return "Pilot";
                case "co-pilot":
                case "copilot":
                case "co pilot":
                    return "Co-Pilot";
                case "cabin crew":
                case "cabincrew":
                    return "Cabin Crew";
                default:
                    System.out.println("\n Invalid role ! Please enter Pilot, Co-Pilot or Cabin Crew.");
            }
        }
    }


}

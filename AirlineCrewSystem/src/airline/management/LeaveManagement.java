//check leave menu
package airline.management;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import airline.database.Database;
import airline.model.Crew;
import airline.model.Flight;
import airline.model.Leave;
import airline.notification.Notifier;
import airline.notification.EmailNotifier;


public class LeaveManagement {
	
	private Database db;
	private CrewStatusManagement manageStatus;
    private Scanner in = new Scanner(System.in);
    private Notifier emailNotifier = new EmailNotifier();
    
	
    //---------------------------- CONSTRUCTOR ----------------------------
    public LeaveManagement(Database db) {
    	this.db = db;
    	this.manageStatus = new CrewStatusManagement(db);
    }
    
    
    //------------------------ LEAVE MANAGEMENT MENU ------------------------
    public void viewLeaveMenu() {
    	int choice = 0;       
    	do{
			System.out.println("\n\n ==============================================");
			System.out.println(" |            LEAVE MANAGEMENT MENU           |");
			System.out.println(" ==============================================");
			System.out.println(" |  1. View All Leave Records                 |");
			System.out.println(" |  2. Add Leave Record                       |");
			System.out.println(" |  3. Approve / Reject Leave Record          |");
			System.out.println(" |  4. Update Leave Record                    |");
			System.out.println(" |  5. Delete Leave Record                    |");
			System.out.println(" |  6. Search Leave Record by crew ID         |");
			System.out.println(" |  7. Back to Main Menu                      |");
			System.out.println(" ==============================================");
			System.out.print(" Enter your choice (1-7): ");


            //Input validation
			if (in.hasNextInt()) {
                choice = in.nextInt();
                in.nextLine();
            } else {
                System.out.println("\n Invalid input. Enter a number 1-7.");
                in.nextLine();
                continue;
            }

            switch(choice){
                case 1:
                    viewAllLeave();
                    break;
                case 2:
                    addLeave();
                    break;
                case 3:
                    appRej();
                    break;
                case 4:
                    updateLeave();
                    break;
                case 5:
                    deleteLeave();
                    break;
                case 6:
                    searchLeaveByCrewID();
                    break;
                case 7:
                    System.out.println("\n Returning to Main Menu...");
                    return;
                default:
                    System.out.println("\n Invalid choice. Please choose 1 to 7.");
            }
        } while(choice!=7);
    }

    
    //----------------------- VIEW ALL LEAVE ------------------------
    public void viewAllLeave(){
        System.out.println("\n\n-----------------------------------------------------Leave List----------------------------------------------------");
        System.out.println(" =================================================================================================================");
        System.out.printf(" | %-7s | %-6s | %-20s | %-30s | %-10s | %-10s | %-8s |%n", "LeaveID", "CrewID", "     Crew Name", "            Reason", "StartDate", " EndDate", " Status");
        System.out.println(" =================================================================================================================");
        for (Leave l : db.getLeaveList()) {
            String crewName = "N/A";
            for (Crew c : db.getCrewList()) {
                if (c.getId().equalsIgnoreCase(l.getCrewID())) {
                    crewName = c.getName();
                    break;
                }
            }
            System.out.printf(" | %-7s | %-6s | %-20s | %-30s | %-10s | %-10s | %-8s |%n", l.getLeaveID(),l.getCrewID(), crewName, l.getReason(), l.getStartDate(), l.getEndDate(), l.getStatus());
        }
        System.out.println(" =================================================================================================================");
        System.out.println(" Total leave: " + db.getLeaveList().size());
    }

    
    //------------------------ ADD NEW LEAVE ------------------------
    public void addLeave() {
        System.out.println("\n\n ------------------- Add New Leave Record -------------------");
        String leaveID = generateNextLeaveID();
        System.out.println(" New Leave ID generated: " + leaveID);

        // Get valid crew id from user
        String crewID = "";
        while (true) {
            System.out.print(" Enter Crew ID: ");
            String id = in.nextLine().trim().toUpperCase();
            if (id.isEmpty()) {
                System.out.println(" Crew ID cannot be empty!");
                continue;
            }
            boolean crewExists = db.getCrewList().stream().anyMatch(c -> c.getId().equalsIgnoreCase(id));
            if (!crewExists) {
                System.out.println(" Crew ID not found in database!");
                continue;
            }
            crewID = id;
            break;
        }

        // Read valid start and end dates
        LocalDate startDate = readDate(" Enter Leave Start Date (YYYY-MM-DD or YYYYMMDD): ");
        LocalDate endDate = null;
        while (endDate == null) {
            endDate = readDate(" Enter Leave End Date (YYYY-MM-DD or YYYYMMDD): ");
            if (endDate.isBefore(startDate)) {
                System.out.println(" Invalid end date, it is before the start date. Please reenter again");
                endDate = null;
            }
        }

        System.out.print(" Enter Reason: ");
        String reason = in.nextLine();
        if (reason.isEmpty()) {
            reason = "N/A";
        }

        // Add leave to database with default status "Pending"
        db.getLeaveList().add(new Leave(leaveID, crewID, reason, startDate, endDate, "Pending"));
        System.out.println(" Leave record added successfully! (Status: Pending)");
    }
    
    
    //------------------------ READ DATE ------------------------
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();
            if (input.isEmpty()) {
                return null;  // user pressed Enter → keep current
            }
            try {
                if (input.contains("-")) { // format YYYY-MM-DD
                    return LocalDate.parse(input);
                } else if (input.length() == 8) { // format YYYYMMDD
                    return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyyMMdd"));
                } else {
                    System.out.println(" Invalid format! Try again.");
                }
            } catch (Exception e) {
                System.out.println(" Invalid date! Try again.");
            }
        }
    }

    
    
    //------------------------ AUTO-GENERATED LEAVE ID ------------------------
    private String generateNextLeaveID() {
        int max = 0;

        for (Leave l : db.getLeaveList()) {
            String id = l.getLeaveID();

            if (id.startsWith("L")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        int next = max + 1;
        return "L" + String.format("%03d", next);
    }

    
    //------------------------ APPROVE / REJECT LEAVE ------------------------
    public void appRej(){
    	System.out.println("\n\n ------------------------------------- Approve / Reject Leave Request -------------------------------------");
    	viewAllLeave();
    	System.out.print("\n Enter Leave ID: ");
        String searchID = in.nextLine().trim();

        Leave record = findLeave(searchID);
        if (record == null){
        	System.out.println(" Leave ID not found");
            return;
        }

        System.out.print(" Enter Action (A = Approve / R = Reject): ");
        String action = in.nextLine().trim().toUpperCase();

        switch (action){
            case "A":
            	//check if the crew has assigned to flight
            	String leaveCrewID = record.getCrewID();
            	LocalDateTime leaveStart = record.getStartDate().atStartOfDay();
            	LocalDateTime leaveEnd = record.getEndDate().atTime(23,59);
            	
            	Crew crew = db.getCrewList().stream().filter(c-> c.getId().equalsIgnoreCase(leaveCrewID)).findFirst().orElse(null);
         
            	if(crew == null) {
            		System.out.println(" Crew ID not found.");
            		return;
            	}
            	
            	boolean conflict = false;
            	for (Flight flight : crew.getAssignedFlights()) {
            		if(!(flight.getArrivalDate().isBefore(leaveStart) || flight.getDepartureDate().isAfter(leaveEnd))) {
            			conflict = true;
            			System.out.println("\n !!! Warning: Crew is assigned to flight " + flight.getId() + " !!!");
            		}
            	}
           		
            	if(conflict) {
            		System.out.println("\n Please remove the crew from the flight before approving the leave.");
            		System.out.println(" Leave approved unsuccessful!");
            		System.out.println(" Leave status remains: " + record.getStatus());
            		break;
            	}
                      
                record.setStatus("Approved");
                manageStatus.addStatus(leaveCrewID, leaveStart, leaveEnd, "On leave", null);
                System.out.println(" Leave approved successfully!");
                
             // ------------------- Notify crew via email about approved leave -------------------
                emailNotifier.notify(" Hello " + crew.getName() + ", your leave " + record.getLeaveID() + " has been approved. Please check your schedule.");

                break;       
            case "R":
                record.setStatus("Rejected");
                System.out.println(" Leave rejected successfully!");
                
                // ------------------- Notify crew via email about rejected leave -------------------
                Crew crewR = db.getCrewList().stream().filter(c -> c.getId().equalsIgnoreCase(record.getCrewID())).findFirst().orElse(null);
                if (crewR != null) {
                    EmailNotifier emailNotifier = new EmailNotifier();
                    emailNotifier.notify(" Hello " + crewR.getName() + ", your leave " + record.getLeaveID() + " has been rejected. Please check your schedule.");
                }
                
                break;
            default:
                System.out.println(" Invalid action.");
        }
    }
    
    //------------------------ FIND LEAVE ------------------------
    public Leave findLeave(String leaveID) {
        for (Leave l : db.getLeaveList()) {
            if (l.getLeaveID().equalsIgnoreCase(leaveID)) {
                System.out.println("\n Search Result:");
                System.out.println(" =======================================================");
                System.out.printf(" |%-18s|  %-30s  |%n", " Leave ID", l.getLeaveID());
                System.out.printf(" |%-18s|  %-30s  |%n", " Crew ID", l.getCrewID());
                System.out.printf(" |%-18s|  %-30s  |%n", " Leave Date(From) ", l.getStartDate());
                System.out.printf(" |%-18s|  %-30s  |%n", " Leave Date(To) ", l.getEndDate());
                System.out.printf(" |%-18s|  %-30s  |%n", " Reason ", l.getReason());
                System.out.printf(" |%-18s|  %-30s  |%n", " Status ", l.getStatus());
                System.out.println(" =======================================================");
                return l;
            }
        }
        System.out.println(" Leave record not found!");
        return null;
    }

    
    // ------------------------ UPDATE LEAVE ------------------------
    public void updateLeave() {
        viewAllLeave();
        System.out.println("\n\n --------------------------- Update Existing Leave Record ---------------------------");

        String searchID = "";
        while (searchID.isEmpty()) {
            System.out.print(" Enter Leave ID: ");
            searchID = in.nextLine().trim().toUpperCase();
            if (searchID.isEmpty())
                System.out.println("\n Please enter a leave ID");
        }

        Leave record = findLeave(searchID);
        if (record == null) {
            System.out.println(" Leave ID not found.");
            return;
        }

        // Remove old crew leave status if previously approved
        if ("Approved".equalsIgnoreCase(record.getStatus())) {
            Crew crew = db.getCrewList().stream().filter(c -> c.getId().equalsIgnoreCase(record.getCrewID())).findFirst().orElse(null);
            if (crew != null) {
                manageStatus.removeOldRecords(record.getCrewID(), record.getStartDate().atStartOfDay(),record.getEndDate().atTime(23, 59));
            }
        }

        System.out.print(" Update Crew ID [Press Enter to keep current]: ");
        String newCrewID = in.nextLine().trim();
        if (!newCrewID.isBlank()) {
            boolean crewExists = db.getCrewList().stream().anyMatch(c -> c.getId().equalsIgnoreCase(newCrewID));
            if (!crewExists)
                System.out.println(" Invalid Crew ID! CrewID not updated.");
            else {
                record.setCrewID(newCrewID.toUpperCase());
            }
        }

        System.out.print(" Update reason [Press Enter to keep current]: ");
        String newReason = in.nextLine().trim();
        if (!newReason.isBlank())
            record.setReason(newReason);


        // Flexible date input
        LocalDate newStartDate = readDate(" Update new Start Date [Press Enter to keep current] (YYYY-MM-DD or YYYYMMDD): ");
        LocalDate newEndDate = readDate(" Update new End Date [Press Enter to keep current] (YYYY-MM-DD or YYYYMMDD): ");

        // Validate date logic
        if (newStartDate != null && newEndDate != null) {
            if (newStartDate.isAfter(newEndDate))
                System.out.println(" Invalid leave date! Leave start date and end date not updated.");
            else {
                record.setStartDate(newStartDate);
                record.setEndDate(newEndDate);
            }
        } else {
            if (newStartDate != null) {
                if (newStartDate.isAfter(record.getEndDate()))
                    System.out.println(" Invalid start date! Start date not updated.");
                else {
                    record.setStartDate(newStartDate);
                }
            }
            if (newEndDate != null) {
                if (newEndDate.isBefore(record.getStartDate()))
                    System.out.println(" Invalid end date! End date not updated.");
                else {
                    record.setEndDate(newEndDate);
                }
            }
        }

        record.setStatus("Pending");
        System.out.println("\n Leave updated successfully!\n Caution: Leave status has been reset to Pending.");
    }

    
    // ------------------------ DELETE LEAVE ------------------------
    public void deleteLeave(){
    	viewAllLeave();
    	System.out.println("\n\n ----------------------- Delete Leave Records -----------------------");
    	String searchID = "";
    	while(searchID.isEmpty()) {
    		System.out.print(" Enter Leave ID: ");
    		searchID = in.nextLine().trim().toUpperCase();
    		if(searchID.isEmpty())
    		System.out.println("\n Please enter a leave ID");
    	}
    	
        Leave record = findLeave(searchID);
        if (record == null){
        	System.out.println(" Leave ID not found.");
            return;
        }
    	
        System.out.print(" Are you sure you want to delete this record? (Y/N): ");
        String confirm = in.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
        	// delete the crew status stored if leave status is approved
        	if ("Approved".equalsIgnoreCase(record.getStatus())) {
            	Crew crew = db.getCrewList().stream().filter(c->c.getId().equalsIgnoreCase(record.getCrewID())).findFirst().orElse(null);
            	if(crew != null) {
            		manageStatus.removeOldRecords(record.getCrewID(), record.getStartDate().atStartOfDay(), record.getEndDate().atTime(23,59));
            	}
            }
        	
            db.getLeaveList().remove(record);
            System.out.println("\n Record deleted successfully.");
        } else {
        	System.out.println("\n Delete operation cancelled.");
        }
    }

    
    // ------------------------ SEARCH LEAVE BY CREW ID ------------------------
    public void searchLeaveByCrewID(){
    	System.out.println("\n\n ----------------------- Search Leave Records -----------------------");
        System.out.print(" Enter Crew ID: ");
        String crewID = in.nextLine().toUpperCase().trim();

        if(crewID.isBlank()){
            System.out.println(" Crew ID cannot be empty.");
            return;
        }
        
        String crewName = "N/A";
        for(Crew c : db.getCrewList()){
            if(c.getId().equalsIgnoreCase(crewID)){
                crewName = c.getName();
                break;
            }
        }

        boolean found = false;
        System.out.println("\n Search result for crew ID: " + crewID+ " (Name: " + crewName + ")");
        System.out.println(" =================================================================================");
        System.out.printf(" | %-7s | %-30s | %-10s | %-10s | %-8s |%n", "LeaveID", "           Reason", "StartDate", " EndDate", " Status");
        System.out.println(" =================================================================================");
        
        for (Leave l : db.getLeaveList()) {
            if(l.getCrewID().equalsIgnoreCase(crewID)){
            	System.out.printf(" | %-7s | %-30s | %-10s | %-10s | %-8s |%n", l.getLeaveID(), l.getReason(), l.getStartDate(), l.getEndDate(), l.getStatus());
            	found = true;
            }
        }
            
        if (!found){
            System.out.println(" |   N/A   |               N/A              |     N/A    |     N/A    |    N/A   |");
            System.out.println(" =================================================================================");
            System.out.println("\n No leave records found for Crew ID: " + crewID);
            return;
        }
        System.out.println(" =================================================================================");
    }
}


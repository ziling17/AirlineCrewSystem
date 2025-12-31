package airline.main;

import java.util.Scanner;
import airline.database.Database;
import airline.management.CrewManagement;
import airline.management.FlightManagement;
import airline.management.LeaveManagement;
import airline.management.FlightHoursTracking;


public class Main {
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		Database db = new Database();
		
		db.loadSampleData();
		
		int choice = 0;
		
		// Main menu loop
		do {
			System.out.println("\n\n ========================================");
			System.out.println(" |    AIRLINE CREW MANAGEMENT SYSTEM    |");
			System.out.println(" ========================================");
			System.out.println(" |  1. Crew Management                  |");
			System.out.println(" |  2. Flight Schedule Management       |");
			System.out.println(" |  3. Leave Management                 |");
			System.out.println(" |  4. Flight Hours Tracking            |");
			System.out.println(" |  5. Exit                             |");
			System.out.println(" ========================================");
			System.out.print(" Enter your choice: ");
			
            // Validate input (must be an integer)
			if (in.hasNextInt()) {
                choice = in.nextInt();
                in.nextLine();
            } else {
                System.out.println("\n Invalid input ! Enter a number between 1-5.");
                in.nextLine();
                continue;
            }
			
			switch (choice) {
			
            case 1:
                CrewManagement crewMng = new CrewManagement(db);
                crewMng.viewCrewMenu();
                break;
            
            case 2:
                FlightManagement flightMng = new FlightManagement(db);
                flightMng.viewFlightMenu();
                break;
            
            case 3:
            	LeaveManagement leaveMng = new LeaveManagement(db);
                leaveMng.viewLeaveMenu();
                break;
            
            case 4:
            	FlightHoursTracking tracking = new FlightHoursTracking(db);
                tracking.displayWeeklyFlightHoursRisk();
                break;
            
            case 5:
                System.out.println("\n Exiting system. Thank you!");
                break;
            
            default:
                System.out.println(" Invalid choice! Please enter 1-5.");
			}	
		} while (choice != 5);
		
		in.close();
	}
}

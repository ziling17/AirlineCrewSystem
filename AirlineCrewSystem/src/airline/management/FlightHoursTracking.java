package airline.management;

import airline.database.Database;
import airline.model.Crew;

public class FlightHoursTracking {

    private Database db;
    
	//---------------------------- CONSTRUCTOR ----------------------------
    public FlightHoursTracking(Database db) {
        this.db = db;
    }

	//--------- DISPLAY WEEKLY FLIGHT HOURS AND FATIGUE RISK LEVEL ---------
    public void displayWeeklyFlightHoursRisk() {

        System.out.println("\n ---------------- Weekly Flight Hours & Risk Monitoring ----------------");
        System.out.println(" =============================================================");
        System.out.printf(" | %-6s | %-20s | %-10s | %-12s |\n", "CrewID", "Name", "Hours", "Risk Level");
        System.out.println(" =============================================================");

        for (Crew crew : db.getCrewList()) {
            double hours = crew.getFlightHours();
            String risk;

            if (hours > 55) risk = "CRITICAL";
            else if (hours >= 50) risk = "HIGH";
            else if (hours >= 40) risk = "MODERATE";
            else risk = "LOW";

            System.out.printf(" | %-6s | %-20s | %-10.2f | %-12s |\n",crew.getId(), crew.getName(), hours, risk);
        }

  
        System.out.println(" =============================================================");
        System.out.println(" CAAM CAD 1901: Maximum 55 duty hours in any 7 consecutive days.");
        System.out.println(" This module supports fatigue risk identification and crew safety.");
    }
}


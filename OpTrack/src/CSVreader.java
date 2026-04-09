//consulted with gemini https://gemini.google.com/share/a317f50956de
//reviewed doc https://www.codecademy.com/article/time-complexity-of-merge-sort#heading-what-is-merge-sort
//reviewed doc https://www.w3schools.com/dsa/dsa_timecomplexity_theory.php
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

public class CSVreader {

    public static void main(String[] args) throws Exception {
        OpportunityManager manager = new OpportunityManager();

        Path path = Path.of("OpTrack/src/data.csv");
        try (BufferedReader br = Files.newBufferedReader(path)) {// br if it fails, it will throw an exc
            String headers = br.readLine();//reads 1st line into header and buffer downs
            if (headers == null){
                System.out.println("Empty file");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) { //using null uses 0 bytes of memory
                String[] cols = line.split(",");

                String name = cols[0];
                String type = cols[1];
                String link = cols[2];
                LocalDate deadline = LocalDate.parse(cols[3].trim());

                Opportunity opportunity = manager.createOpp(name, type, link, deadline);
                manager.addNew(opportunity);
            }
            //sorts all opportunities
            //have the internship and scholarship lists ready
            manager.sortAll();
            manager.mainQueue();
        }
        Scanner scanner = new Scanner(System.in);
        while(true) {
            //User menu
            System.out.println("-----Main Menu-----");
            System.out.println("1. View table");
            System.out.println("2. View Internship List");
            System.out.println("3. View Scholarship List");
            System.out.println("4. Add Opportunity");
            System.out.println("5. Search by name");
            System.out.println("6. View deadlines");
            System.out.println("7. View expired opportunities");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            //displays all opportunities
            if (choice == 1) {
                manager.displayAll();
                //displays only internships
            } else if (choice == 2) {
                System.out.println("----Internships----");
                manager.displayInternship();
                //displays only scholarships
            } else if (choice == 3) {
                System.out.println("----Scholarships----");
                manager.displayScholarship();
                //add new opportunity to the database
            } else if (choice == 4) {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();

                System.out.print("Type of opportunity: ");
                String type = scanner.nextLine();

                System.out.print("Enter link: ");
                String link = scanner.nextLine();

                System.out.print("Enter deadline (YYYY-MM-DD): ");
                String dateInput = scanner.nextLine();
                LocalDate deadline = LocalDate.parse(dateInput);

                //takes input and adds to the database
                //re-sorts opportunities
                Opportunity opportunity = manager.createOpp(name, type, link, deadline);
                manager.addNew(opportunity);
                manager.sortAll();
                manager.mainQueue();
                manager.saveFile(path.toString());

                System.out.println("Added to list");
                //searches opportunity
            } else if (choice == 5) {
                System.out.print("Enter name to search: ");
                String searchName = scanner.nextLine();

                Opportunity found = manager.searchByName(searchName);
                if (found != null) {
                    found.displayInfo();
                } else {
                    System.out.println("Not on list");
                }
                //displays upcoming deadline
            } else if (choice == 6) {
                Opportunity next = manager.nextUpcoming();
                if (next != null) {
                    System.out.println("Upcoming deadline:");
                    next.displayInfo();
                } else {
                    System.out.println("No upcoming deadlines.");
                }
                //displays expired deadlines
            } else if (choice == 7) {
                manager.listExpiredQueue();
                //gives the option to update/remove opportunity
                manager.displayExpired(scanner);
            } else if (choice == 8) {
                System.out.println("Ending program");
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }
}

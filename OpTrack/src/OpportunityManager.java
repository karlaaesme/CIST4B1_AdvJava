import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

//manages the heavy work
public class OpportunityManager {
    HashTableEntry hashTable = new HashTableEntry(11);

    private final ArrayList<Opportunity> internship = new ArrayList<>();
    private final ArrayList<Opportunity> scholarship = new ArrayList<>();

    private Queue<Opportunity> upcomingDeadline = new Queue<>();
    private Queue<Opportunity> expiredQueue = new Queue<>();

    //method to save changes in the database
    public void saveFile(String filePath){
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
            pw.println("Name,Type,Link,Deadline");

            ArrayList<Opportunity> all = new ArrayList<>();
            all.addAll(internship);
            all.addAll(scholarship);

            for (Opportunity opp : all){
                String csvLine = String.format("%s,%s,%s,%s", opp.getName(),opp.getType(),opp.getLink(),opp.getDeadline().toString());
                pw.println(csvLine);
            }
            System.out.println("Changes added to database");
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }
    //created opportunity object
    public Opportunity createOpp(String name,String type, String link, LocalDate deadline) {
        if(type.equalsIgnoreCase("Internship")){
            return new Internship(name, type, link, deadline);
        } else{
            return new Scholarship(name, type, link, deadline);
        }
    }
    //add an opportunity
    //checks the type of opp to add to their respective list
    public void addNew(Opportunity opp){
        hashTable.put(opp.getName(), opp);

        if(opp instanceof Internship){
            internship.add(opp);
        }else if(opp instanceof Scholarship){
            scholarship.add(opp);
        }
    }
    //combines all opp and sort them by deadline
    //adds only the upcoming deadlines to the queue
    public void mainQueue(){
        ArrayList<Opportunity> combine = new ArrayList<>();
        combine.addAll(internship);
        combine.addAll(scholarship);

        mergeSort(combine);
        upcomingDeadline = new Queue<>();

        for (int i = 0; i < combine.size(); i++) {
            Opportunity opp = combine.get(i);
            if (opp.getDeadline().isAfter(java.time.LocalDate.now())) {
                upcomingDeadline.enqueue(opp);
            }
        }
    }
    //same process as mainQueue but only add the expired deadlines to another queue
    public void listExpiredQueue(){
        expiredQueue = new Queue<>();
        ArrayList<Opportunity> combine = new ArrayList<>();
        combine.addAll(internship);
        combine.addAll(scholarship);

        for (int i = 0; i < combine.size(); i++) {
            Opportunity opp = combine.get(i);
            if (opp.getDeadline().isBefore(java.time.LocalDate.now())) {
                expiredQueue.enqueue(opp);
            }
        }
    }
    public Opportunity nextUpcoming(){
        return upcomingDeadline.peek();
    }
    public Opportunity searchByName(String name){
        return hashTable.get(name);
    }
    //remove deadline from database
    public void removeExpired(String name){
        Opportunity opp = hashTable.get(name);
        if (opp == null){
            System.out.println("Not found");
            return;
        }
        hashTable.remove(name);
        if (opp instanceof Internship){
            internship.remove(opp);
        } else if (opp instanceof Scholarship) {
            scholarship.remove(opp);
        }
        System.out.println("Removed Successfully");

        sortAll();
        mainQueue();
        listExpiredQueue();
    }
    //sorts all opportunnities
    public void sortAll(){
        mergeSort(internship);
        mergeSort(scholarship);
    }
    //------merge sort------
    private void mergeSort(ArrayList<Opportunity> list){
        if(list.size() <= 1)
            return;
        int middle = list.size()/2;
        ArrayList<Opportunity> left = new ArrayList<>(list.subList(0,middle));
        ArrayList<Opportunity> right = new ArrayList<>(list.subList(middle, list.size()));

        mergeSort(left);
        mergeSort(right);
        merge(list, left, right);
    }
    private void merge(ArrayList<Opportunity> list, ArrayList<Opportunity> left, ArrayList<Opportunity> right){
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()){
            if(left.get(i).getDeadline().compareTo(right.get(j).getDeadline()) <= 0){
                list.set(k, left.get(i));
                i++;
            }else{
                list.set(k, right.get(j));
                j++;
            }
            k++;
        }
        while(i < left.size()){
            list.set(k, left.get(i));
            i++; k++;
        }
        while (j < right.size()){
            list.set(k, right.get(j));
            j++; k++;
        }
    }
    //---------------------------------------

    //displays all - menu option 1
    public void displayAll() {
        for (Opportunity o : internship) {
            o.displayInfo();
        }
        for (Opportunity o : scholarship) {
            o.displayInfo();
        }
    }
    //displays scholarships - menu option 3
    public void displayScholarship(){
        if (scholarship.isEmpty()){
            System.out.println("No scholarships on list");
            return;
        }
        for (Opportunity opp : scholarship){
            opp.displayInfo();
        }
    }
    //displays internships - menu option 2
    public void displayInternship(){
        if (internship.isEmpty()){
            System.out.println("No internships on list");
            return;
        }
        for (Opportunity opp : internship){
            opp.displayInfo();
        }
    }
    //displays expired opp - menu option 7
    public void displayExpired(java.util.Scanner scanner) {
        if (expiredQueue.isEmpty()) {
            System.out.println("No expired opportunities.");
            return;
        }
        System.out.println("Expired list: ");
        //temp queue holds opp to modify them if need it
        Queue<Opportunity> temp = new Queue<>();
        while (expiredQueue.isEmpty() == false) {
            Opportunity opp = expiredQueue.dequeue();

            opp.displayInfo();

            System.out.println("1. Update");
            System.out.println("2. Remove");
            System.out.println("3. Skip");//return to this one

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter new deadline on format (YYYY-MM-DD): ");
                String newDate = scanner.nextLine();
                try {
                    java.time.LocalDate newDeadline = java.time.LocalDate.parse(newDate);
                    opp.setDeadline(newDeadline);
                    sortAll();
                    mainQueue();
                    saveFile("OpTrack/src/data.csv");
                    System.out.println("Deadline updated");
                } catch (Exception e) {
                    System.out.println("Invalid date.");
                }
                temp.enqueue(opp);
            } else if (choice == 2) {
                removeExpired(opp.getName());
                saveFile("OpTrack/src/data.csv");
            } else {
                temp.enqueue(opp);
            }
        }
        while (temp.isEmpty() == false) {
            expiredQueue.enqueue(temp.dequeue());
        }
        //takes the opp that were not modify and puts them back to the list of expired deadlines
        listExpiredQueue();
    }
}


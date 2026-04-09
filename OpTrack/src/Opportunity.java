import java.time.LocalDate;
//parent class
public abstract class Opportunity {
    String name;
    String type;
    String link;
    LocalDate deadline;


        public Opportunity(String name, String type, String link, LocalDate deadline) {
            this.name = name;
            this.type = type;
            this.link = link;
            this.deadline = deadline;

        }

        public abstract void displayInfo();
        public String getName() {
            return name;
        }
        public String getLink(){
            return link;
        }
        public LocalDate getDeadline(){
            return deadline;
        }
        public void setDeadline(LocalDate deadline){
            this.deadline = deadline;
        }

        public String getType() {
            return type;
        }
    }
    //child
    class Internship extends Opportunity{

        public Internship(String name, String type, String link, LocalDate deadline){
            super(name, type, link, deadline);
        }

        @Override
        public void displayInfo() {
            System.out.println(name + " Dealine: " + deadline);
            System.out.println("Link: " + link);
        }
    }
    //child
    class Scholarship extends Opportunity {

        public Scholarship(String name, String type, String link, LocalDate deadline) {
            super(name, type, link, deadline);
        }

        @Override
        public void displayInfo() {
            System.out.println(name + " Dealine: " + deadline);
            System.out.println("Link: " + link);
        }
}

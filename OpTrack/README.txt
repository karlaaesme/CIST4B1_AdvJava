---Setup and Installation
-Download/Extract: Unzip the project folder Salinas_Midterm_Project.zip.
-Open Project
-Verify File Path: Ensure the data.csv file is located in the directory specified in the CSVreader class (default is OpTrack/src/data.csv).
-Library Imports: The project uses standard Java libraries (java.util, java.time, java.io). No external .jar files are required.

---How to Run
-Locate the main class (CSVreader).
-Right-click the file and select Run.
-Choose an option from the menu

---Testing Instructions
To verify the data structures and algorithms are working correctly:
-Search Test: Select the search option and type a name exactly as it appears in the CSV. The program should return the result instantly via the Hash Table.
-Sorting Test: Add a new entry with an old date (e.g., 2023-01-01). Run the "Review Expired" option; the new entry should appear in the correct chronological order due to Merge Sort.
-Collision Test: (For technical review) The Hash Table uses Linear Probing. If two names hash to the same index, the program will successfully store and retrieve both. Example use: NASA and MESA.
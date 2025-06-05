/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class AirlineManagement {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of AirlineManagement
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public AirlineManagement(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end AirlineManagement

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            AirlineManagement.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      AirlineManagement esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the AirlineManagement object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new AirlineManagement (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");

                //**the following functionalities should only be able to be used by Management**
                System.out.println("1. View Flights");
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Flight Schedule");

                //**the following functionalities should only be able to be used by customers**
                System.out.println("10. Search Flights");
                System.out.println("11. Find Flight Ticket Cost");
                System.out.println("12. Find Flight Type");
                System.out.println("13. Make Reservation");

                //**the following functionalities should ony be able to be used by Pilots**
                System.out.println("15. Maintenace Request");

               //**the following functionalities should ony be able to be used by Technicians**
                System.out.println("16. Get Repairs Performed");
                System.out.println("17. List Pilot Requests");
                System.out.println("18. Log a Repair");

                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: ViewFlights(esql, authorisedUser); break;
                   case 5: ViewFullOrderHistory(esql, authorisedUser); break;
                   case 6: GetFlightWeekSchedule(esql, authorisedUser); break;
                   case 10: SearchFlights(esql, authorisedUser); break;
                   case 11: FindFlightTickeCost(esql, authorisedUser); break;
                   case 12: FindFlightPlaneType(esql, authorisedUser); break;
                   case 13: MakeReservation(esql, authorisedUser); break;
                   case 15: MakeMaintenanceRequest(esql, authorisedUser); break;
                   case 16: GetRepairs(esql, authorisedUser); break;
                   case 17: ListPilotMaintenanceRequests(esql, authorisedUser); break;
                   case 18: CatologRepair(esql, authorisedUser); break;




                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice




   /*
    * Creates a new user
    **/
   public static void CreateUser(AirlineManagement esql)
   {
      try
      {
         System.out.print("Username: ");
         String username = in.readLine().trim();

         // Check if entered username is available:
         String checkUsername = "SELECT username FROM users WHERE username = '" + username + "';";
         int usernameTaken = esql.executeQuery(checkUsername);
         if (usernameTaken > 0)
         {
            System.out.println("Username is already taken.");
            return;
         }

         System.out.print("Password: ");
         String password = in.readLine().trim();

         System.out.print("Select Role (Customer, Pilot, Technician, Manager): ");
         String role = in.readLine().trim();

         // Create SQL to INSERT new user to the 'user' TABLE.
         // INSERT INTO Users (username, password, role) VALUES (username, password, role):
         String sql = "INSERT INTO Users (username, password, role) VALUES ('" + username + "', '" + password + "', '" + role + "')";
         
         esql.executeUpdate(sql);
         System.out.println("User created! Now can log in.");
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
      }
      
   }//end CreateUser





   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(AirlineManagement esql)
   {
      try
      {
         // Get username and password:
         System.out.print("Enter username: ");
         String username = in.readLine().trim();
         System.out.print("Enter password: ");
         String password = in.readLine().trim();
         
         // Check username and password:
         // Get users from User TABLE.
         String query = "SELECT password, role FROM users WHERE username = '" + username + "';";
         List<List<String>> results = esql.executeQueryAndReturnResult(query);

         // If results is empty, then no such user exists in User TABLE.
         if (results.size() <= 0)
         {
            System.out.println("Username does not exist.");
            return null;
         }
         else
         {
            // Check if password is correct.
            if (results.get(0).get(0).equals(password))
            {
               System.out.println("Log in successful. Welcome " + username + "!");
               return username;
            }
            else
            {
               System.out.println("Incorrect password.");
               return null;
            }
         }
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return null;
      }
   }//end


   // Helper Funciton for checking user role.
   public static boolean isRole(AirlineManagement esql, String user, String targetRole)
   {
      try
      {
         // Query to get user from users table:
         String getUserQuery = "SELECT role FROM users WHERE username = '" + user + "';";

         // Fetch user from users table using the query:
         List<List<String>> results = esql.executeQueryAndReturnResult(getUserQuery);
         System.out.println("User role is: " + results.get(0).get(0));

         if (!results.get(0).get(0).equals(targetRole))
         {
            return false;
         }
         else
         {
            return true;
         }
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return false;
      }
   }


// Rest of the functions definition go in here






/******************************************
 * 
 * * * * MANAGEMENT FUNCTIONALITIES * * * * 
 * 
 ******************************************/




   public static void ViewFlights(AirlineManagement esql, String user)
   {

   }


   public static void ViewFullOrderHistory(AirlineManagement esql, String user)
   {

   }



   // Management: 1. Given a flight number, get the flight’s schedule for the week - A flight may be scheduled on multiple days in a week
   public static void GetFlightWeekSchedule(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a manager. If user is not a manager, user cannot do this function.
         if (!isRole(esql, user, "Manager"))
         {
            System.out.println("You are NOT a manager. You cannot view flight week schedule.");
            return;
         }

         System.out.print("Enter flight number: ");
         String flightNumber = in.readLine().trim();

         // These are dummy dates for demoing:
         String dummyStartOfWeekDate1 = "2025-05-05";
         String dummyEndOfWeekDate1   = "2025-05-11";

         String dummyStartOfWeekDate2 = "2025-05-12";
         String dummyEndOfWeekDate2   = "2025-05-18";

         String query = "SELECT s.dayofweek, fi.flightdate, s.departuretime, s.arrivaltime, f.departurecity, f.arrivalcity " 
                      + "FROM flightinstance fi JOIN schedule s ON s.flightnumber = fi.flightnumber AND s.dayofweek = TO_CHAR(fi.flightdate, 'FMDay') " 
                      + "JOIN flight f ON f.flightnumber = fi.flightnumber WHERE f.flightnumber = '" + flightNumber + "' AND flightdate BETWEEN '" + dummyStartOfWeekDate1 + "' AND '" + dummyEndOfWeekDate1 + "' ORDER BY flightdate;";

         // Check if query returns any results. If it does, print result.
         int isResults = esql.executeQuery(query);
         if (isResults > 0)
         {
            // Fetch data from table using query and print:
            int result = esql.executeQueryAndPrintResult(query);
         }
         else
         {
            System.out.println("No such data exists. Try again.");
         }
         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }










/*************************************************************
 * 
 * * * * * * * * * CUSTOMER FUNCTIONALITIES * * * * * * * * * 
 * 
 *************************************************************/


   // Customers: 1. Given a destination and departure city, find all flights on a given date. - Must return departure and arrival time, number of stops scheduled, and on-time record (as a percentage)
   public static void SearchFlights(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a customer. If user is not a customer, user cannot do this function.
         if (!isRole(esql, user, "Customer"))
         {
            System.out.println("You are NOT a customer. You cannot search flights.");
            return;
         }


         // Prompt user for input:
         System.out.print("Enter departure city: ");
         String departureCity = in.readLine().trim();

         System.out.print("Enter arrival city: ");
         String arrivalCity = in.readLine().trim();

         System.out.print("Enter flight date (yyyy-mm-dd): ");
         String flightDate = in.readLine().trim();


         // Query to get the flight information (departure city, arrival city, number of stops, and on-time record):
         String query = "SELECT s.departuretime, s.arrivaltime, fi.numofstops,ROUND( 100 * AVG( (fi.departedontime AND fi.arrivedontime)::int),1) AS ontimepercentage "
                      + "FROM flightinstance fi JOIN schedule s ON s.flightnumber = fi.flightnumber AND s.dayofweek = TO_CHAR(fi.flightdate, 'FMDay') " 
                      + "JOIN flight f ON f.flightnumber = fi.flightnumber WHERE departurecity = '" + departureCity + "' AND arrivalcity = '" + arrivalCity + "' AND flightdate = '" + flightDate + "' " 
                      + "GROUP BY  s.departuretime, s.arrivaltime, fi.numofstops;";


         // Check if query returns any results. If it does, print result.
         int isResults = esql.executeQuery(query);
         if (isResults > 0)
         {
            // Fetch data from table using query and print:
            int result = esql.executeQueryAndPrintResult(query);
         }
         else
         {
            System.out.println("No such data exists. Try again.");
         }

         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }// end SearchFlights



   // Customers: 2. Given a flight number, find the ticket cost.
   public static void FindFlightTickeCost(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a customer. If user is not a customer, user cannot do this function.
         if (!isRole(esql, user, "Customer"))
         {
            System.out.println("You are NOT a customer. You cannot find flight ticket cost.");
            return;
         }

         // Prompt user for input:
         System.out.print("Enter flight number: ");
         String flightNumber = in.readLine().trim();

         System.out.print("Enter flight date (yyyy-mm-dd): ");
         String flightDate = in.readLine().trim();


         // Get fligt info and print:
         String query = "SELECT ticketcost FROM flightinstance WHERE flightnumber = '" + flightNumber + "' AND flightdate = '" + flightDate + "';";

         // Check if query returns any results. If it does, print result.
         int isResults = esql.executeQuery(query);
         if (isResults > 0)
         {
            // Fetch data from table using query and print:
            int result = esql.executeQueryAndPrintResult(query);
         }
         else
         {
            System.out.println("No such data exists. Try again.");
         }

         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }


   // Customers: 3. Given a flight number, find the airplane type (make and model)
   public static void FindFlightPlaneType(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a customer. If user is not a customer, user cannot do this function.
         if (!isRole(esql, user, "Customer"))
         {
            System.out.println("You are NOT a customer. You cannot find flight plane type.");
            return;
         }

         // Prompt user for input:
         System.out.print("Enter flight number: ");
         String flightNumber = in.readLine().trim();


         // Query to get make and model of a flight given a flight number.
         String query = "SELECT p.make, p.model FROM flight f JOIN plane p ON f.planeid = p.planeid WHERE f.flightnumber = '" + flightNumber + "';";

         // Check if query returns any results. If it does, print result.
         int isResults = esql.executeQuery(query);
         if (isResults > 0)
         {
            // Fetch data from table using query and print:
            int result = esql.executeQueryAndPrintResult(query);
         }
         else
         {
            System.out.println("No such data exists. Try again.");
         }

         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }














   // Customers: 4. Make a reservation for a flight - Get on the waitlist for a flight if the flight is full (Still need to fix)
   public static void MakeReservation(AirlineManagement esql, String user)
   {
      try
      {












         // First, check if user is a customer. If not, cannot do this function.
         String query = "SELECT role FROM users WHERE username = '" + user + "';";
         List<List<String>> results = esql.executeQueryAndReturnResult(query);
         System.out.println("Role is: " + results.get(0).get(0));

         if (!results.get(0).get(0).equals("Customer"))
         {
            System.out.println("You are not a customer. You cannot search flights.");
            return;
         }

         System.out.print("Enter flight number: ");
         String flightNumber = in.readLine().trim();

         System.out.print("Enter flight date (yyyy-mm-dd): ");
         String flightDate = in.readLine().trim();

         System.out.print("Enter flight day (Monday, Tuesday, Thursday, Friday, Saturday, Sunday): ");
         String flightDay = in.readLine().trim();

         System.out.print("Enter customer id: ");
         String customerID = in.readLine().trim();

         String getLastID = "SELECT CAST(SUBSTRING(MAX(reservationid) FROM 2) AS INTEGER)+1 FROM reservation;";
         List<List<String>> lastReservationID = esql.executeQueryAndReturnResult(getLastID);
         String newReservationID = "R" + lastReservationID.get(0).get(0);
         System.out.println(newReservationID);

         String getFlightQuery = "SELECT (seatstotal - seatssold), flightinstanceid AS seatsavailable FROM flight f JOIN flightinstance fi  USING (flightnumber) JOIN schedule s USING (flightnumber) WHERE flightnumber = '" + flightNumber + "' AND flightdate = '" + flightDate + "' AND dayofweek = '" + flightDay + "';";
         List<List<String>> result    = esql.executeQueryAndReturnResult(getFlightQuery);

         String status = "reserved";
         int seatsAvailable = Integer.parseInt(result.get(0).get(0));

         int flightInstanceID = Integer.parseInt(result.get(0).get(1));

         if (seatsAvailable < 1)
         {
            status = "waitlist";
         }

         // Need to update seats sold if reserved (increment seats for that flight).
         if (status == "reserved")
         {
            String updateFlightInstanceQuery = "UPDATE flightinstance SET seatssold = seatssold+1 WHERE flightinstanceid = '" + flightInstanceID + "';";
            esql.executeUpdate(updateFlightInstanceQuery);
         }

         String makeReservationQuery = "INSERT INTO reservation (reservationid, customerid, flightinstanceid, status) VALUES ('" + newReservationID + "', '" + customerID + "', '" + flightInstanceID + "', '" + status + "')";
         esql.executeUpdate(makeReservationQuery);
         System.out.println("Reservation made. You are " + status + ".");
         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }








/******************************************
 * 
 * * * * TECHNICIAN FUNCTIONALITIES * * * * 
 * 
 ******************************************/


   // Maintenance: 1. Given a plane ID and a date range, list all the dates and the codes for repairs performed
   public static void GetRepairs(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a technician. If user is not a technician, user cannot do this function.
         if (!isRole(esql, user, "Technician"))
         {
            System.out.println("You are NOT a technician. You cannot see repairs performed.");
            return;
         }

         // Prompt user for input:
         System.out.print("Enter plane id: ");
         String planeID = in.readLine().trim();

         System.out.print("Enter start date: ");
         String startDate = in.readLine().trim();

         System.out.print("Enter end date: ");
         String endDate = in.readLine().trim();

         String query = "SELECT repairdate, repaircode FROM repair WHERE planeid = '" + planeID + "' AND repairdate BETWEEN '" + startDate + "' AND '" + endDate + "' ORDER BY repairdate;";

         // Check if query returns any results. If it does, print result.
         int isResults = esql.executeQuery(query);
         if (isResults > 0)
         {
            // Fetch data from table using query and print:
            int result = esql.executeQueryAndPrintResult(query);
         }
         else
         {
            System.out.println("No such data exists. Try again.");
         }
         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }


   // Maintenance: 2. Given a pilot ID, list all maintenance requests made by the pilot
   public static void ListPilotMaintenanceRequests(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a technician. If user is not a technician, user cannot do this function.
         if (!isRole(esql, user, "Technician"))
         {
            System.out.println("You are NOT a technician. You cannot see pilot maintenance requests.");
            return;
         }

         System.out.print("Enter pilot id: ");
         String pilotID = in.readLine().trim();

         String query = "SELECT m.requestdate, m.repaircode, p.make, p.model, p.year, p.lastrepairdate FROM maintenancerequest m JOIN plane p ON m.planeid = p.planeid WHERE pilotid = '" + pilotID + "';";

         // Check if query returns any results. If it does, print result.
         int isResults = esql.executeQuery(query);
         if (isResults > 0)
         {
            // Fetch data from table using query and print:
            int result = esql.executeQueryAndPrintResult(query);
         }
         else
         {
            System.out.println("No such data exists. Try again.");
         }

         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }


   // Maintenance: 3. After each repair, make an entry showing plane ID, repair code, and date of repair (still need to fix).
   public static void CatologRepair(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if the user is a technician. If user is not a technician, user cannot do this function.
         if (!isRole(esql, user, "Technician"))
         {
            System.out.println("You are NOT a technician. You cannot see repairs performed.");
            return;
         }

         System.out.print("Enter technician id: ");
         String techID = in.readLine().trim();

         System.out.print("Enter repair date: ");
         String repairDate = in.readLine().trim();

         System.out.print("Enter repair code id: ");
         String repairCode = in.readLine().trim();

         System.out.print("Enter plane id: ");
         String planeID = in.readLine().trim();

         // Make new repairid:
         String getLastRepairID = "SELECT MAX(repairid) FROM repair;";
         List<List<String>> r = esql.executeQueryAndReturnResult(getLastRepairID);
         int newRepairID = Integer.parseInt(r.get(0).get(0)) + 1;

         String sql = "INSERT INTO repair (repairid, planeid, repaircode, repairdate, technicianid) VALUES ('" + newRepairID + "', '" + planeID + "', '" + repairCode + "', '" + repairDate + "', '" + techID + "')";
         esql.executeUpdate(sql);
         System.out.println("Repair has been logged!");
         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }







/******************************************
 * 
 * * * * PILOT FUNCTIONALITIES * * * * 
 * 
 ******************************************/



   // Pilot: 1. Make maintenance request listing plane ID, repair code requested, and date of request (still need to fix).
   public static void MakeMaintenanceRequest(AirlineManagement esql, String user)
   {
      try
      {
         // First, check if user is a customer. If not, cannot do this function.
         String query = "SELECT role FROM users WHERE username = '" + user + "';";
         List<List<String>> results = esql.executeQueryAndReturnResult(query);
         System.out.println("Role is: " + results.get(0).get(0));

         if (!results.get(0).get(0).equals("Pilot"))
         {
            System.out.println("You are not a pilot. You cannot make maintenance requests.");
            return;
         }

         System.out.print("Enter pilot id: ");
         String pilotID = in.readLine().trim();

         System.out.print("Eneter plane id: ");
         String planeID = in.readLine().trim();

         System.out.print("Eneter repair code: ");
         String repairCode = in.readLine().trim();

         System.out.print("Eneter date of request: ");
         String requestDate = in.readLine().trim();


         // Make new requestid:
         String getLastRequestID = "SELECT MAX(requestid) FROM maintenancerequest;";
         List<List<String>> r = esql.executeQueryAndReturnResult(getLastRequestID);
         int newRequestID = Integer.parseInt(r.get(0).get(0)) + 1;
         
         
         String sql = "INSERT INTO maintenancerequest (requestid, planeid, repaircode, requestdate, pilotid) VALUES ('" + newRequestID + "', '" + planeID + "', '" + repairCode + "', '" + requestDate + "', '" + pilotID + "')";
         esql.executeUpdate(sql);
         System.out.println("Request made!");

         return;
      }
      catch (Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }



   public static void feature1(AirlineManagement esql) {}
   public static void feature2(AirlineManagement esql) {}
   public static void feature3(AirlineManagement esql) {}
   public static void feature4(AirlineManagement esql) {}
   public static void feature5(AirlineManagement esql) {}
   public static void feature6(AirlineManagement esql) {}
  


}//end AirlineManagement


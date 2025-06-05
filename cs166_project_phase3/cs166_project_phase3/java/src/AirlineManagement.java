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
                if (isRole(esql, authorisedUser, "Manager")) {
					System.out.println("1. View Flight Schedule");
                	System.out.println("2. View Flight Seats Available/Sold");  
                	System.out.println("3. View Flight Departed/Arrive On Time");
                	System.out.println("4. View Flights Today");
                	System.out.println("5. View Passengers Status");
                	System.out.println("6. View Reserved Passenger Information");
                	System.out.println("7. View Plane Information");
                	System.out.println("8. View Technician Repairs");
                	System.out.println("9. View Repairs for a Plane");
                	System.out.println("10. View Flight Statistics");

					System.out.println("20. Log out");

					switch (readChoice()){
				   		case 1: ViewFlightSchedule(esql, authorisedUser); break;
                   		case 2: ViewFlightSeatsAvailableSold(esql, authorisedUser); break;
                		case 3: ViewFlightOnTime(esql, authorisedUser); break;
               			case 4: ViewFlightsToday(esql, authorisedUser); break;
               			case 5: ViewPassengerStatus(esql, authorisedUser); break;
               			case 6: ViewPassengerInformation(esql, authorisedUser); break;
               			case 7: ViewPlaneInformation(esql, authorisedUser); break;
                   		case 8: ViewTechnicianRepairs(esql, authorisedUser); break;
                		case 9: ViewPlaneRepairs(esql, authorisedUser); break;
               			case 10: ViewFlightStatistics(esql, authorisedUser); break;

						case 20: usermenu = false; break;
                   		default : System.out.println("Unrecognized choice!"); break;
					}
				}

                //**the following functionalities should only be able to be used by customers**
				if (isRole(esql, authorisedUser, "Customer")) {
					System.out.println("1. Search Flights");
                	System.out.println("2. Find Flight Ticket Cost");
                	System.out.println("3. Find Flight Type");
                	System.out.println("4. Make Reservation");

					System.out.println("20. Log out");

					switch (readChoice()){
				   		case 1: SearchFlights(esql, authorisedUser); break;
                		case 2: FindFlightTickeCost(esql, authorisedUser); break;
                   		case 3: FindFlightPlaneType(esql, authorisedUser); break;
                   		case 4: MakeReservation(esql, authorisedUser); break;

						case 20: usermenu = false; break;
                   		default : System.out.println("Unrecognized choice!"); break;
					}
				}

                //**the following functionalities should ony be able to be used by Pilots**
				if (isRole(esql, authorisedUser, "Pilot")) {
					System.out.println("1. Maintenace Request");

					System.out.println("20. Log out");

					switch (readChoice()){
				   		case 1: MakeMaintenanceRequest(esql, authorisedUser); break;

						case 20: usermenu = false; break;
                   		default : System.out.println("Unrecognized choice!"); break;
					}
				}

               	//**the following functionalities should ony be able to be used by Technicians**
				if (isRole(esql, authorisedUser, "Pilot")) {
                	System.out.println("1. Get Repairs Performed");
                	System.out.println("2. List Pilot Requests");
                	System.out.println("3. Log a Repair");

                	System.out.println("20. Log out");
                
					switch (readChoice()){
                   		case 15: GetRepairs(esql, authorisedUser); break;
                   		case 16: ListPilotMaintenanceRequests(esql, authorisedUser); break;
                   		case 17: CatologRepair(esql, authorisedUser); break;

                   		case 20: usermenu = false; break;
                   		default : System.out.println("Unrecognized choice!"); break;
                	}
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
   public static void CreateUser(AirlineManagement esql){
		try{
			System.out.print("Username: ");
			String username  = in.readLine().trim();

			// Check if entered username is available
			String checkUsername = "SELECT username FROM users WHERE username = \'" + username + "\';";
			int usernameTaken = esql.executeQuery(checkUsername);
			
			if (usernameTaken > 0) {
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
      	} catch (Exception e) {
         	System.err.println(e.getMessage());
		}
   }//end CreateUser

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(AirlineManagement esql){
      	try {
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
         	if (results.size() <= 0) {
            	System.out.println("Username does not exist.");
            	return null;
         	} else {
           		// Check if password is correct.
            	if (results.get(0).get(0).equals(password)) {
               	System.out.println("Log in successful. Welcome " + username + "!");
               	return username;
        		} else {
               	System.out.println("Incorrect password.");
               	return null;
            	}
         	}
      	} catch (Exception e) {
         	System.err.println(e.getMessage());
         	return null;
      	}
   }//end

// Helper Funciton for checking user role.
   public static boolean isRole(AirlineManagement esql, String user, String targetRole) {
      try {
        // Query to get user from users table:
        String getUserQuery = "SELECT role FROM users WHERE username = '" + user + "';";

        // Fetch user from users table using the query:
        List<List<String>> results = esql.executeQueryAndReturnResult(getUserQuery);
        //System.out.println("User role is: " + results.get(0).get(0));

        if (!results.get(0).get(0).equals(targetRole)) {
            return false;
        } else {
            return true;
        }
      } catch (Exception e) {
        System.err.println(e.getMessage());
        return false;
    }
}

/******************************************
 * 
 * * * * MANAGEMENT FUNCTIONALITIES * * * * 
 * 
 ******************************************/

	// Management 1
   	public static void ViewFlightSchedule(AirlineManagement esql, String user) {
      	try {
			// Input:
        	System.out.print("Enter flight number: ");
        	String flightNumber = in.readLine().trim();

        	// Get flight schedule:
        	String getScheduleQuery = "SELECT DepartureCity, ArrivalCity, DayOfWeek, DepartureTime, ArrivalTime "
									+ "FROM Flight JOIN Schedule USING (FlightNumber) "
									+ "WHERE FlightNumber = \'" + flightNumber + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getScheduleQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getScheduleQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
   	}

	// Management 2
	public static void ViewFlightSeatsAvailableSold(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter flight number: ");
        	String flightNumber = in.readLine().trim();

			System.out.print("Enter flight date (mm-dd-yy): ");
        	String flightDate = in.readLine().trim();

        	// Get flight seats:
        	String getSeatsQuery 	= "SELECT (SeatsTotal - SeatsSold) AS SeatsAvailable, SeatsSold "
									+ "FROM Flight JOIN FlightInstance USING (FlightNumber) "
									+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getSeatsQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getSeatsQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 3
	public static void ViewFlightOnTime(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter flight number: ");
        	String flightNumber = in.readLine().trim();

			System.out.print("Enter flight date (mm-dd-yy): ");
        	String flightDate = in.readLine().trim();

        	// Get valid flight:
        	String getValidQuery 	= "SELECT * "
									+ "FROM Flight JOIN FlightInstance USING (FlightNumber) "
									+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\';";
        	
			// Check if query is valid.
        	int isResults = esql.executeQuery(getValidQuery);
         	if (isResults > 0) {
				// Get flight depart:
				String getDepartQuery 	= "SELECT * "
										+ "FROM Flight JOIN FlightInstance USING (FlightNumber) "
										+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\' AND DepartedOnTime;";
				isResults = esql.executeQuery(getDepartQuery);
				if (isResults > 0) {
					System.out.println("Flight departed on time.");
				} else {
					System.out.println("Flight did not depart on time.");
				}

				// Get flight arrive:
				String getArriveQuery 	= "SELECT * "
										+ "FROM Flight JOIN FlightInstance USING (FlightNumber) "
										+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\' AND ArrivedOnTime;";
				isResults = esql.executeQuery(getArriveQuery);
				if (isResults > 0) {
					System.out.println("Flight arrived on time.");
				} else {
					System.out.println("Flight did not arrive on time");
				}
         	} else {
            	System.out.println("No such data exists. Try again.");
				return;
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 4
	public static void ViewFlightsToday(AirlineManagement esql, String user) {
		try {
        	// Input:
			System.out.print("Enter flight date (mm-dd-yy): ");
        	String flightDate = in.readLine().trim();

        	// Get flight number:
        	String getNumberQuery 	= "SELECT FlightNumber "
									+ "FROM Flight JOIN FlightInstance USING (FlightNumber) "
									+ "WHERE FlightDate = \'" + flightDate + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getNumberQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getNumberQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 5
	public static void ViewPassengerStatus(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter flight number: ");
        	String flightNumber = in.readLine().trim();

			System.out.print("Enter flight date (mm-dd-yy): ");
        	String flightDate = in.readLine().trim();

			// Get valid flight:
        	String getValidQuery 	= "SELECT * "
									+ "FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID) "
									+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\';";
        	
			// Check if query is valid.
			int isResults = esql.executeQuery(getValidQuery);
			if (isResults == 0) {
				System.out.println("No such data exists. Try again.");
				return;
			}

        	// Get reserved:
        	String getReservedQuery 	= "SELECT (CustomerID) AS ReserevedPassenger "
										+ "FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID) "
										+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\' AND Status = 'reserved';";
        	
			// Check if query returns any results. If it does, print result.
			isResults = esql.executeQuery(getReservedQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getReservedQuery);
         	} else {
            	System.out.println("No one reserved a flight.");
         	}

			// Get waitlist:
        	String getWaitlistQuery 	= "SELECT (CustomerID) AS WaitlistPassenger "
										+ "FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID) "
										+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\' AND Status = 'waitlist';";
        	
			// Check if query returns any results. If it does, print result.
        	isResults = esql.executeQuery(getWaitlistQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getWaitlistQuery);
         	} else {
            	System.out.println("No one is on the waitlist.");
         	}

			// Get flown:
        	String getFlownQuery 	= "SELECT (CustomerID) AS FlownPassenger "
									+ "FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID) "
									+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate = \'" + flightDate + "\' AND Status = 'flown';";
        	
			// Check if query returns any results. If it does, print result.
        	isResults = esql.executeQuery(getFlownQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getFlownQuery);
         	} else {
            	System.out.println("No one has flown.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 6
	public static void ViewPassengerInformation(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter reservation ID: ");
        	String reservationID = in.readLine().trim();

        	// Get passenger information:
        	String getCustomerQuery = "SELECT FirstName, LastName, Gender, DOB, Address, Phone, Zip "
									+ "FROM Reservation JOIN Customer USING (CustomerID) "
									+ "WHERE ReservationID = \'" + reservationID + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getCustomerQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getCustomerQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 7
	public static void ViewPlaneInformation(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter plane ID: ");
        	String planeID = in.readLine().trim();

        	// Get plane infromation:
        	String getPlaneQuery 	= "SELECT Make, Model, Year, LastRepairDate "
									+ "FROM Plane "
									+ "WHERE PlaneID = \'" + planeID + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getPlaneQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getPlaneQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 8
	public static void ViewTechnicianRepairs(AirlineManagement esql, String user) {
		try {
        	try {
        	// Input:
        	System.out.print("Enter technician ID: ");
        	String technicianID = in.readLine().trim();

			// Get valid flight:
        	String getValidQuery 	= "SELECT * "
									+ "FROM Technician "
									+ "WHERE TechnicianID = \'" + technicianID + "\';";;
        	
			// Check if query is valid.
			int isResults = esql.executeQuery(getValidQuery);
			if (isResults == 0) {
				System.out.println("No such data exists. Try again.");
				return;
			}

        	// Get repairs:
        	String getRepairQuery 	= "SELECT RepairID, PlaneID, RepairCode, RepairDate "
									+ "FROM Technician JOIN Repair USING (TechnicianID) "
									+ "WHERE TechnicianID = \'" + technicianID + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	isResults = esql.executeQuery(getRepairQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getRepairQuery);
         	} else {
            	System.out.println("The technician made no repairs");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 9
	public static void ViewPlaneRepairs(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter plane ID: ");
        	String planeID = in.readLine().trim();

			System.out.print("Enter startDate (yyyy-mm-dd): ");
        	String startDate = in.readLine().trim();

			System.out.print("Enter endDate (yyyy-mm-dd): ");
        	String endDate = in.readLine().trim();

        	// Get plane infromation:
        	String getReapirQuery 	= "SELECT RepairDate, RepairCode "
									+ "FROM Plane JOIN Repair USING (PlaneID) "
									+ "WHERE PlaneID = \'" + planeID + "\' AND RepairDate BETWEEN \'" + startDate + "\' AND \'" + endDate + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getReapirQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getReapirQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
      	}
	}

	// Management 10 (Not Done)
	public static void ViewFlightStatistics(AirlineManagement esql, String user) {
		try {
        	// Input:
        	System.out.print("Enter flight number: ");
        	String flightNumber = in.readLine().trim();

			System.out.print("Enter startDate (mm-dd-yy): ");
        	String startDate = in.readLine().trim();

			System.out.print("Enter endDate (mm-dd-yy): ");
        	String endDate = in.readLine().trim();

        	// Get plane infromation:
        	String getFlightStatQuery 	= "SELECT COUNT(*) AS FlightDays, SUM(SeatsSold) AS SoldTotal, SUM(SeatsTotal - SeatsSold) AS UnsoldTotal "
										+ "FROM Flight JOIN FlightInstance USING (FlightNumber) "
										+ "WHERE FlightNumber = \'" + flightNumber + "\' AND FlightDate BETWEEN \'" + startDate + "\' AND \'" + endDate + "\';";
        	
			// Check if query returns any results. If it does, print result.
        	int isResults = esql.executeQuery(getFlightStatQuery);
         	if (isResults > 0) {
            	// Fetch data from table using query and print:
            	int result = esql.executeQueryAndPrintResult(getFlightStatQuery);
         	} else {
            	System.out.println("No such data exists. Try again.");
         	}
      	} catch (Exception e) {
        	System.err.println(e.getMessage());
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
      try {
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
// Rest of the functions definition go in here

   public static void feature1(AirlineManagement esql) {}
   
}//end AirlineManagement


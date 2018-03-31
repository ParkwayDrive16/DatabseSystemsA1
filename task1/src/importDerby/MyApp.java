package importDerby;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;


public class MyApp
{
	private static String driver = "org.apache.derby.jdbc.ClientDriver";
	private static String dbName = "MyDB";
	private static String connectionURL = "jdbc:derby:" + dbName + ";create=true";
	private static String createBusiness = "create table businessNames (" + 
			"id integer not null, " + 
			"name varchar(200) not null, " + 
			"status varchar(20) not null, " + 
			"registerDate date, " + 
			"cancelDate date, " + 
			"renewDate date, " + 
			"stateNumber varchar(10), " + 
			"state varchar(3), " +
			"primary key(id))";
	private static String createABN = "create table abns (" + 
			"businessId int not null references businessNames(id), " + 
			"abn varchar(20) not null, " + 
			"primary key(businessId, abn))";
	private static String tableName = "BusinessNames";
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;

    public static void main(String[] args) throws IOException, SQLException
    {
	    String[] tokens = new String[9];
	    BufferedReader input = null;
	    String filename = "dataset.csv";
	    String delimiter = "\t";
	    String line;
	    input = new BufferedReader(new FileReader(filename));
	    int globalCounter = 0;
	    int abnCounter = 0;
	    String initialUpdateBN = "insert into " + tableName + " (id, name, status, registerDate, cancelDate, renewDate,"
        		+ "stateNumber, state) values ";
	    String initialUpdateABN = "insert into abns (businessId, abn) values ";
	    StringBuilder commandForBusiness = new StringBuilder(initialUpdateBN);
	    StringBuilder commandForABN = new StringBuilder(initialUpdateABN);
	    
	    createConnection();
	    createDatabase();
	    stmt = conn.createStatement();
	    
	    Instant start = Instant.now();
	    line = input.readLine();
	    
	    while ((line = input.readLine()) != null) {
	    	globalCounter++;
	    	tokens = line.split(delimiter, -1);
	    	tokens[1] = tokens[1].replaceAll("'", "''");
	    	
	    	for (int i = 0; i < 3; i++) {
	    		if(tokens[i+3].equals("")) {
	    			tokens[i+3] = null;
	    		} else {
	    		tokens[i+3] = tokens[i+3].replaceAll("/",".");
	    		tokens[i+3] = "'" + tokens[i+3] + "'";
	    		}
	    	}
	    	
	    	commandForBusiness.append("("+globalCounter+",'" +tokens[1]+"','"+tokens[2]+"',"+tokens[3]+","+tokens[4]+","+tokens[5]+",'"+tokens[6]+"','"+tokens[7]+"'),");
	    	if (!tokens[8].equals("")) {
	    		commandForABN.append("(" + globalCounter +",'" + tokens[8] + "'),");
	    		abnCounter++;
	    	}
	    	
	    	if (globalCounter % 100 == 0 )
	    	{
	    		commandForBusiness.setLength(commandForBusiness.length() - 1);
//	    		System.out.println(commandForBusiness.toString());
	    		sendToDatabase(commandForBusiness.toString());	    		
	    		commandForBusiness.setLength(0);
	    		commandForBusiness.append(initialUpdateBN);
	    		if (abnCounter != 0) {
	    			commandForABN.setLength(commandForABN.length() - 1);
	    			sendToDatabase(commandForABN.toString());
//	    			System.out.println(commandForABN.toString());
	    			commandForABN.setLength(0);
		    		commandForABN.append(initialUpdateABN);
		    		abnCounter = 0;
	    		}
	    	}
	    	
	    	
	    	if(globalCounter % 50000 == 0) {
	    		printInfo(globalCounter, start);
	    	}
	    }
	    
	    if (globalCounter % 100 != 0)
	    {
	    	commandForBusiness.setLength(commandForBusiness.length() - 1);
//	    	System.out.println(commandForBusiness);
		    sendToDatabase(commandForBusiness.toString());
	    	if (abnCounter != 0) {
	    		commandForABN.setLength(commandForABN.length() - 1);
//		    	System.out.println(commandForABN.toString());
			    sendToDatabase(commandForABN.toString());
	    	}
	    }
	    
	    conn.commit();
	    printInfo(globalCounter, start);
	    System.out.println("Import finished successfully.");
	    shutdown();
	    input.close();
    }
    
    private static void createConnection()
    {
        try
        {
            Class.forName(driver);
            //Get a connection
            conn = DriverManager.getConnection(connectionURL);
            conn.setAutoCommit(false);
        }
        catch (Exception except)
        {
            except.printStackTrace();
            System.err.println("Failed to create connection!");
        }
    }
    
    private static void createDatabase() 
    {
    	try {
    		stmt = conn.createStatement();
			stmt.executeUpdate(createBusiness);
			stmt.executeUpdate(createABN);
    	}
		catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
            System.err.println("Failed to create database!");
        }

    }
    
    
    private static void sendToDatabase(String command) 
    {
    	try {
			stmt.executeUpdate(command);
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
    }
    
    private static void printInfo(int records, Instant startTime) 
    {
    	System.out.println("Added " + records + " records!");
    	Instant end = Instant.now();
    	System.out.println("Time taken: "+ Duration.between(startTime, end).getSeconds() +" seconds");
    }

    
    private static void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                conn.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            System.err.println("Failed to close the connection!");
        }

    }
}
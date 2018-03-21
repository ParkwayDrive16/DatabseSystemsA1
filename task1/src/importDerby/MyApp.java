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
	private static String createString = "create table businessNames (id integer not null generated always as identity (start with 1, increment by 1), name varchar(200) not null, status varchar(20) not null, registerDate date, cancelDate date, renewDate date, stateNumber varchar(10), state varchar(3), abn varchar(20), primary key(id))";
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
	    int counter = 1;
	    String initialString = "insert into " + tableName + " (name, status, registerDate, cancelDate, renewDate,"
        		+ "stateNumber, state, abn) values ";
	    StringBuilder command = new StringBuilder(initialString);
	    
	    createConnection();
	    createDatabase();
	    stmt = conn.createStatement();
	    
	    Instant start = Instant.now();
	    line = input.readLine();
	    
	    while ((line = input.readLine()) != null) {
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
	    	
	    	command.append("('" +tokens[1]+"','"+tokens[2]+"',"+tokens[3]+","+tokens[4]+","+tokens[5]+",'"+tokens[6]+"','"+tokens[7]+"','"+tokens[8]+"')");
	    	if (counter % 100 == 0 )
	    	{
	    		sendToDatabase(command.toString());
	    		command.setLength(0);
	    		command.append(initialString);
	    	} else {
	    		command.append(",");
	    	}
	    	
	    	counter++;
	    	if(counter % 50000 == 0) {
	    	System.out.println("Added " + counter + " records!");
	    	Instant end = Instant.now();
	    	System.out.println("Time taken: "+ Duration.between(start, end).getSeconds() +" seconds");
	    	}
	    }
	    command.setLength(command.length() - 1);
	    sendToDatabase(command.toString());
	    
	    System.out.println("Added " + counter + " records!");
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
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
    
    private static void createDatabase() 
    {
    	try {
    		stmt = conn.createStatement();
			stmt.executeUpdate(createString);
    	}
		catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
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
                DriverManager.getConnection(connectionURL + ";shutdown=true");
                conn.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            
        }

    }
}
package searchDerby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Scanner;
import java.sql.ResultSetMetaData;


public class SearchDerby
{
  private static String driver = "org.apache.derby.jdbc.ClientDriver";
  private static String dbURL = "jdbc:derby:MyDB";
  private static String tableName = "businessNames";
  // jdbc Connection
  private static Connection conn = null;
  private static Statement stmt = null;
  static Scanner reader;
  
  static Instant start;
  static Instant end;
  
  public static void main(String[] args)
  {
      createConnection();
      while(true) {
        String input = getUserInput();
        if (input.equals("x"))
          break;
        
        selectBusinessNames(input);
        System.out.println("Time taken: "+ (end.toEpochMilli() - start.toEpochMilli()) +" milliseconds");
      }
      reader.close();
      shutdown();
  }
  
  private static void createConnection()
  {
    try
    {
        Class.forName(driver).newInstance();
        //Get a connection
        conn = DriverManager.getConnection(dbURL); 
    }
    catch (Exception except)
    {
        except.printStackTrace();
        System.err.println("Failed to create connection!");
    }
  }
  
  private static void selectBusinessNames(String seachName)
  {
    try
    {
      stmt = conn.createStatement();
      String statement = ("select * from " + tableName +" where name = '"+seachName+"'");
      ResultSet results = stmt.executeQuery(statement);
      start = Instant.now();
      ResultSetMetaData rsmd = results.getMetaData();
      int numberCols = rsmd.getColumnCount();
      for (int i=1; i<=numberCols; i++)
      {
        //print Column Names
        System.out.print(rsmd.getColumnLabel(i)+"\t");
      }

      System.out.println("\n-------------------------------------------------");

      while(results.next())
      {
          int id = results.getInt(1);
          String name = results.getString(2);
          String status = results.getString(3);
          String regDate = ((results.getDate(4) == null) ? "" : results.getDate(4).toString());
          String canDate = ((results.getDate(5) == null) ? "" : results.getDate(4).toString());
          String renDate = ((results.getDate(6) == null) ? "" : results.getDate(4).toString());
          String stateNumber = results.getString(7);
          String state = results.getString(8);
          
          
          
          System.out.println(id + "\t" + name + "\t" + status + "\t" +regDate+ "\t"  +
              "\t" +canDate+ "\t" +renDate+ "\t"+ stateNumber + "\t" + state);
        }
        end = Instant.now();
        results.close();
        stmt.close();
      }
      catch (SQLException sqlExcept)
      {
          sqlExcept.printStackTrace();
          System.out.println("Error");
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
              DriverManager.getConnection(dbURL + ";shutdown=true");
              conn.close();
          }           
      }
      catch (SQLException sqlExcept)
      {
          
      }
  }
  
  private static String getUserInput() {
    String stringtoSearch;
    reader = new Scanner(System.in);
    while(true) {
    System.out.println("Enter name to seach: ");
    stringtoSearch = reader.nextLine();
    if (!stringtoSearch.equals("\n"))
      break;
    }
    
    return stringtoSearch;
  }
  
}
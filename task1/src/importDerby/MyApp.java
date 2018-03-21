package importDerby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MyApp {
  static Connection conn;

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: java JavaDBDemo <Name> <Address>");
      System.exit(1);
    }
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    String dbName = "AddressBookDB";
    String connectionURL = "jdbc:derby:" + dbName + ";create=true";
    String createString = "CREATE TABLE ADDRESSBOOKTbl (NAME VARCHAR(32) NOT NULL, ADDRESS VARCHAR(50) NOT NULL)";
    Class.forName(driver);

    conn = DriverManager.getConnection(connectionURL);

    Statement stmt = conn.createStatement();
    stmt.executeUpdate(createString);

    PreparedStatement psInsert = conn
        .prepareStatement("insert into ADDRESSBOOKTbl values (?,?)");

    psInsert.setString(1, args[0]);
    psInsert.setString(2, args[1]);

    psInsert.executeUpdate();

    Statement stmt2 = conn.createStatement();
    ResultSet rs = stmt2.executeQuery("select * from ADDRESSBOOKTbl");
    System.out.println("Addressed present in your Address Book\n\n");
    int num = 0;

    while (rs.next()) {
      System.out.println(++num + ": Name: " + rs.getString(1) + "\n Address"
          + rs.getString(2));
    }
    rs.close();
  }
}
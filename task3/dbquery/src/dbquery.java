import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.sql.Date;

public class dbquery 
{

  public static void main(String[] args) throws IOException 
  {
    //creating input file string, string to search for and page size variables
    String inputFile = null;
    String stringToFind = null;
    int pageSize = 0;
    //Checks arguments for string to search for and page size + checks validity
    if(args.length != 0) {
      if(args[0].length() > 0) {
        stringToFind = args[0];
      } else {
        System.out.println("Invalid name. Searching for default string \"BUFFALO LOCKSMITHS\"");
        stringToFind = "BUFFALO LOCKSMITHS";
      }
      if (Util.isInteger(args[1]) && Integer.parseInt(args[1]) > 0) {
        pageSize = Integer.parseInt(args[1]);
      }
      
      inputFile = "../heap." + Integer.toString(pageSize);
      if(!(new File(inputFile).exists())) {
        System.out.println("Input file " +inputFile+ " doesn't exist! Exiting...");
          System.exit(1);
        }
      } else {
        //if no arguments supplied - exits the app
        System.out.println("No argumets supplied! Exiting...");
        System.exit(1);
      }
    //creating input file name based on page size
    
    FileInputStream heap = new FileInputStream(inputFile);
    int pagePointer;
    int resultCount = 0;
    byte[] page = new byte[pageSize];
    //record the start time of the app
    long start = System.currentTimeMillis();
    System.out.println("Trying to match: " + stringToFind);
    while (heap.read(page) > 0) {
      //every loop resets page pointer
      pagePointer = 0;
      //extracts the number of records 
      int recordsCount = Byte.toInt(Arrays.copyOfRange(page, pageSize-4, pageSize));
      for (int i = 0; i < recordsCount; i++) {
        //converting bytes to readable data and storing these values
        int id = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        int nameLength = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        String name = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += nameLength));
        String status = Byte.toStatus(Arrays.copyOfRange(page, pagePointer, pagePointer++));
        Date regDate = Byte.toDate(Arrays.copyOfRange(page, pagePointer, pagePointer += 8));
        Date canDate = Byte.toDate(Arrays.copyOfRange(page, pagePointer, pagePointer += 8));
        Date renewDate = Byte.toDate(Arrays.copyOfRange(page, pagePointer, pagePointer += 8));
        int stateNumLength = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        String stateNum = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += stateNumLength));
        String state = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += 3));
        int abnLength = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        String abn = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += abnLength));
        
        //if name field contains the string we're looking for - create businessRecord class object
        if((name.toLowerCase()).contains(stringToFind.toLowerCase())) {
          businessRecord record = new businessRecord(id, name, status, Util.toLocDate(regDate), 
              Util.toLocDate(canDate), Util.toLocDate(renewDate), stateNum, state, abn);
        //printing matched record
        record.printInfo();
        //increment results count of matched Strings
        resultCount++;
        }
      }
    }
    System.out.println("Matches: " + resultCount);
    heap.close();
    //records the end time of the app and prints in milliseconds
    long end = System.currentTimeMillis();
      System.out.print("\nExecution time: " + (end - start) + " milliseconds.");
  }


}

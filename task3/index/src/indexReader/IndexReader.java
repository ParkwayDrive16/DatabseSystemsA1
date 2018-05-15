package indexReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import util.*;

public class IndexReader {
  static String indexFilepath;
  static String heapFilepath;
  static RandomAccessFile indexFile;
  static RandomAccessFile heapFile;
  static final int INDEX_FILE_SIZE = 3800000;
  static final int SINGLE_RECORD_SIZE = 8;
  static final int DISTANCE_FROM_RECORDSTART = 8;
  static int pageSize = 0;
  
  public static void main(String[] args) throws IOException {
    //getting arguments
    if(args.length != 0) {
      if (Util.isInteger(args[0]) && Integer.parseInt(args[0]) > 0) {
        pageSize = Integer.parseInt(args[0]);
      }
      //set filenames according to argument supplied
      heapFilepath = "../heap." + Integer.toString(pageSize);
      indexFilepath = "../index."  + Integer.toString(pageSize);
    //if file don't exist then exit
    if(!(new File(heapFilepath).exists()) && !(new File(indexFilepath).exists())) {
      System.out.println(indexFilepath + " or "+ heapFilepath +" doesn't exist! Exiting...");
        System.exit(1);
      }
    } else {
      //if no arguments supplied - exits the app
      System.out.println("No argumets supplied! Exiting...");
      System.exit(1);
    }
    //opening both files 
    indexFile = new RandomAccessFile(indexFilepath, "rw");
    heapFile = new RandomAccessFile(heapFilepath, "r");
    //getting a string from the user
    String input = getUserInput();
    //getting hash code of the given string
    int inputHash = input.hashCode();
    //calculating id and posible position in the file
    int id = (int)(Integer.toUnsignedLong(inputHash) % INDEX_FILE_SIZE);
    int position = id*SINGLE_RECORD_SIZE;
    //reading from index file from that position
    //which returns position in heap file
    int heapOffset = readFromIndexFile(position);
    int resultCount = 0;
    //stamp of current time to measure performance
    long start = System.currentTimeMillis();
    while(true) {
      
      if (heapOffset != -1) {
        //if value returned is not -1 then read from heap file and check if strings are equal
        BusinessRecord recordResult = readFromHeapFile(heapOffset);
        if(recordResult.getName().equals(input)) {
          //if string are equal - print the info and 
          //read again in case multiple record with the same name
          recordResult.printInfo();
          //update resultCount
          resultCount++;
          //read from the index file again from current position
          heapOffset = readFromIndexFile((int)indexFile.getFilePointer());
        } else {
          //if strings are not equal then read again in case 
          //the record was moved further when writing index file
          heapOffset = readFromIndexFile((int)indexFile.getFilePointer());
        }
      } else {
        //if -1 returned then end search
        System.out.println("End of results.");
        break;
      }
    }
  //stamp of current time when finished processing
    long end = System.currentTimeMillis();
    //printing result count and time taken
    System.out.println("Matches: " + resultCount);
    System.out.print("\nExecution time: " + (end - start) + " milliseconds.\n");
    //closing heap and index files
    heapFile.close();
    indexFile.close();
  }
  
  private static String getUserInput() {
    //simple method to get input from the user
    String input;
    Scanner reader = new Scanner(System.in);
    
    System.out.println("Enter business name to search: ");
    input = reader.nextLine();
    reader.close();
    return input;
  }
  
  
  private static int readFromIndexFile(int position) throws IOException {
    //change pointer in the index file to particular position
    indexFile.seek(position);
    
    byte[] indexID = new byte[4];
    byte[] indexOffset = new byte[4];
    //read 2 pieces of info 4 bytes long each
    indexFile.read(indexID);
    indexFile.read(indexOffset);
    //check if they are 0
    if(ByteUtil.toInt(indexID) != 0 && ByteUtil.toInt(indexOffset) != 0) {
      //if both are not 0, then return offset in heap file
      return ByteUtil.toInt(indexOffset);
    } else {
      return -1;
    }
    
  }
  
  private static BusinessRecord readFromHeapFile(int offset) throws IOException {
    //updating offset as original points to the name in the heap file
    int heapOffset = offset - DISTANCE_FROM_RECORDSTART;
    heapFile.seek(heapOffset);
    //reading all the info from heap file
    byte[] idBytes = new byte[4];
    heapFile.read(idBytes);
    byte[] nameLengthBytes = new byte[4];
    heapFile.read(nameLengthBytes);
    byte[] nameBytes = new byte[ByteUtil.toInt(nameLengthBytes)];
    heapFile.read(nameBytes);
    byte[] statusBytes = new byte[1];
    heapFile.read(statusBytes);
    byte[] regDateBytes = new byte[8];
    heapFile.read(regDateBytes);
    byte[] canDateBytes = new byte[8];
    heapFile.read(canDateBytes);
    byte[] renewDateBytes = new byte[8];
    heapFile.read(renewDateBytes);
    byte[] stateNumLengthBytes = new byte[4];
    heapFile.read(stateNumLengthBytes);
    byte[] stateNumBytes = new byte[ByteUtil.toInt(stateNumLengthBytes)];
    heapFile.read(stateNumBytes);
    byte[] stateBytes = new byte[3];
    heapFile.read(stateBytes);
    byte[] abnLengthBytes = new byte[4];
    heapFile.read(abnLengthBytes);
    byte[] abnBytes = new byte[ByteUtil.toInt(abnLengthBytes)];
    heapFile.read(abnBytes);
    //creating BusinessRecord object
    BusinessRecord record = new BusinessRecord(ByteUtil.toInt(idBytes), new String(nameBytes), 
        ByteUtil.toStatus(statusBytes), Util.toLocDate(ByteUtil.toDate(regDateBytes)), 
          Util.toLocDate(ByteUtil.toDate(canDateBytes)), 
          Util.toLocDate(ByteUtil.toDate(renewDateBytes)), 
          new String(stateNumBytes), new String(stateBytes), new String(abnBytes));
    //returning the object created
    return record;
    
  }
  
}

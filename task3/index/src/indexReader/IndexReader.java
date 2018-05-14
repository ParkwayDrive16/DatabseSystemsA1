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
  
  public static void main(String[] args) throws IOException {
    int pageSize = 0;
    
    if(args.length != 0) {
      if (Util.isInteger(args[0]) && Integer.parseInt(args[0]) > 0) {
        pageSize = Integer.parseInt(args[0]);
      }
    
      heapFilepath = "../heap." + Integer.toString(pageSize);
      indexFilepath = "../index."  + Integer.toString(pageSize);
    
    if(!(new File(heapFilepath).exists()) && !(new File(indexFilepath).exists())) {
      System.out.println(indexFilepath + " or "+ heapFilepath +" doesn't exist! Exiting...");
        System.exit(1);
      }
    } else {
      //if no arguments supplied - exits the app
      System.out.println("No argumets supplied! Exiting...");
      System.exit(1);
    }

    indexFile = new RandomAccessFile(indexFilepath, "rw");
    heapFile = new RandomAccessFile(heapFilepath, "r");
    
    String input = getUserInput();
    
    int inputHash = input.hashCode();
    int id = (int)(Integer.toUnsignedLong(inputHash) % INDEX_FILE_SIZE);
    int position = id*SINGLE_RECORD_SIZE;
    int heapOffset = readFromIndexFile(position);
    int resultCount = 0;
    
    long start = System.currentTimeMillis();
    while(true) {
      
      if (heapOffset != -1) {
        
        BusinessRecord recordResult = readFromHeapFile(heapOffset);
        if(recordResult.getName().equals(input)) {
          recordResult.printInfo();
          resultCount++;
          heapOffset = readFromIndexFile((int)indexFile.getFilePointer());
        } else {
          heapOffset = readFromIndexFile((int)indexFile.getFilePointer());
        }
      } else {
        System.out.println("End of results.");
        break;
      }
    }
    long end = System.currentTimeMillis();
    System.out.println("Matches: " + resultCount);
    System.out.print("\nExecution time: " + (end - start) + " milliseconds.");
    indexFile.close();
    
  }
  
  private static String getUserInput() {
    String input;
    Scanner reader = new Scanner(System.in);
    
    System.out.println("Enter business name to search: ");
    input = reader.nextLine();
    reader.close();
    return input;
  }
  
  
  private static int readFromIndexFile(int position) throws IOException {
    
    indexFile.seek(position);
    byte[] indexID = new byte[4];
    byte[] indexOffset = new byte[4];
    
    indexFile.read(indexID);
    indexFile.read(indexOffset);
    if(ByteUtil.toInt(indexID) != 0 && ByteUtil.toInt(indexOffset) != 0) {
      return ByteUtil.toInt(indexOffset);
    } else {
      return -1;
    }
    
  }
  
  private static BusinessRecord readFromHeapFile(int offset) throws IOException {
    int heapOffset = offset - DISTANCE_FROM_RECORDSTART;
    heapFile.seek(heapOffset);
    
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
    
    BusinessRecord record = new BusinessRecord(ByteUtil.toInt(idBytes), new String(nameBytes), 
        ByteUtil.toStatus(statusBytes), Util.toLocDate(ByteUtil.toDate(regDateBytes)), 
          Util.toLocDate(ByteUtil.toDate(canDateBytes)), 
          Util.toLocDate(ByteUtil.toDate(renewDateBytes)), 
          new String(stateNumBytes), new String(stateBytes), new String(abnBytes));
    
    return record;
    
  }
  
}

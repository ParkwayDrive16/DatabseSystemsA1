package indexWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import util.*;

public class IndexWriter {
  static String outputFilepath;
  static RandomAccessFile file;
  static String inputFile;
  static final int SKIP_TO_NAME = 4;
  static final int SKIP_TO_STATELENGTH = 25;
  static final int SKIP_TO_ABNLENGTH = 3;
  static final int INT_SIZE = 4;
  static final int INDEX_FILE_SIZE = 3800000;
  static final int SINGLE_RECORD_SIZE = 8;
  
  public static void main(String[] args) throws IOException {
    int pageSize = 0;
    
    if(args.length != 0) {
      if (Util.isInteger(args[0]) && Integer.parseInt(args[0]) > 0) {
        pageSize = Integer.parseInt(args[0]);
      }
    
    inputFile = "../heap." + Integer.toString(pageSize);
    outputFilepath = "../index."  + Integer.toString(pageSize);
    
    if(!(new File(inputFile).exists())) {
      System.out.println("Input file " +inputFile+ " doesn't exist! Exiting...");
        System.exit(1);
      }
    } else {
      //if no arguments supplied - exits the app
      System.out.println("No argumets supplied! Exiting...");
      System.exit(1);
    }
    
    file = new RandomAccessFile(outputFilepath, "rw");
    FileInputStream heap = new FileInputStream(inputFile);
    int pagePointer;
    int pageCounter = 0;
    int heapOffSet;
    byte[] page = new byte[pageSize];
    //stamp of current time to measure performance
    long start = System.currentTimeMillis();
    //loop while there's pages in the heap file
    while (heap.read(page) > 0) {
      //reseting page pointer in every loop
      pagePointer = 0;
      //retrieving number of records from the end of the page
      int recordsCount = ByteUtil.toInt(Arrays.copyOfRange(page, pageSize-4, pageSize));
      //enter the loop that runs recordsCount times
      for (int i = 0; i < recordsCount; i++) {
        //reading and storing only the name of the business
        //saving offset in the heap file for every business name
        pagePointer += SKIP_TO_NAME;
        int nameLength = ByteUtil.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += INT_SIZE));
        heapOffSet = pageCounter * pageSize + pagePointer;
        String name = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += nameLength));
        pagePointer += SKIP_TO_STATELENGTH;
        int stateNumLength = ByteUtil.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += INT_SIZE));
        pagePointer += stateNumLength;
        pagePointer += SKIP_TO_ABNLENGTH;
        int abnLength = ByteUtil.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += INT_SIZE));
        pagePointer += abnLength;
        //getting hash of the name
        int hashName = name.hashCode();
        //calculating position in the idex file
        int id = (int)(Integer.toUnsignedLong(hashName) % INDEX_FILE_SIZE);
        //concatenating id and position in heap file into 8 bytes
        byte[] result = Util.concat(Util.intToByteArray(id),Util.intToByteArray(heapOffSet));
        //writing to the index file
        writeToFile(result, id*SINGLE_RECORD_SIZE);
      }
      //incrementing current page counter
      pageCounter ++;
    }
    //stamp of current time when finished processing
    long end = System.currentTimeMillis();
    System.out.print("\nExecution time: " + (end - start) + " milliseconds.\n");
    //closing heap and index files
    heap.close();
    file.close();
  }
  
  private static void writeToFile(byte[] data, int position) throws IOException {
    //changing the pointer in index file to particular position
    file.seek(position);
    //loop of line probing
    while(true) {
      byte[] bytes = new byte[8];
      //reading 8 bytes to see if they are empty
      file.read(bytes);
      //covert to long and check against 0
      if(ByteUtil.toLong(bytes) == 0 ) {
        //if free then come back 8 bytes and write the data
        file.seek(file.getFilePointer() - SINGLE_RECORD_SIZE);
        file.write(data);
        break;
      }
    }
  }
}

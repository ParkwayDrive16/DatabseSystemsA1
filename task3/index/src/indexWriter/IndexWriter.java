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
    
    long start = System.currentTimeMillis();
    while (heap.read(page) > 0) {
      pagePointer = 0;
      
      int recordsCount = ByteUtil.toInt(Arrays.copyOfRange(page, pageSize-4, pageSize));
      for (int i = 0; i < recordsCount; i++) {
        
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
        
        int hashName = name.hashCode();
        int id = (int)(Integer.toUnsignedLong(hashName) % INDEX_FILE_SIZE);
        
        byte[] result = Util.concat(Util.intToByteArray(id),Util.intToByteArray(heapOffSet));
        writeToFile(result, id*SINGLE_RECORD_SIZE);
      }
      pageCounter ++;
    }
    long end = System.currentTimeMillis();
    System.out.print("\nExecution time: " + (end - start) + " milliseconds.");
    heap.close();
    file.close();
  }
  
  private static void writeToFile(byte[] data, int position) throws IOException {
    file.seek(position);
    while(true) {
      byte[] bytes = new byte[8];
      file.read(bytes);
      
      if(ByteUtil.toLong(bytes) == 0 ) {
        file.seek(file.getFilePointer() - SINGLE_RECORD_SIZE);
        file.write(data);
        break;
      }
    }
  }
}

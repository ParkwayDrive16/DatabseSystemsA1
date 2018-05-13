package indexWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import util.*;

public class IndexWriter {
  static final String FILEPATH = "../indexFile";
  static RandomAccessFile file;

  public static void main(String[] args) throws IOException {
    String inputFile = null;
    file = new RandomAccessFile(FILEPATH, "rw");
    int pageSize = 0;
    
    if(args.length != 0) {
      if (Util.isInteger(args[0]) && Integer.parseInt(args[0]) > 0) {
        pageSize = Integer.parseInt(args[0]);
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
    
    FileInputStream heap = new FileInputStream(inputFile);
    int pagePointer;
    int pageCounter = 0;
    byte[] page = new byte[pageSize];
    
    while (heap.read(page) > 0) {
      pagePointer = 0;
      
      int recordsCount = ByteUtil.toInt(Arrays.copyOfRange(page, pageSize-4, pageSize));
      for (int i = 0; i < recordsCount; i++) {
        
        pagePointer += 4;
        int nameLength = ByteUtil.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        int heapOffSet = pageCounter * pageSize + pagePointer;
        String name = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += nameLength));
        pagePointer += 25;
        int stateNumLength = ByteUtil.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        pagePointer += stateNumLength;
        pagePointer += 3;
        int abnLength = ByteUtil.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
        pagePointer += abnLength;
        
        
        int hashName = name.hashCode();
        int id = (int)(Integer.toUnsignedLong(hashName) % 3800000);
        
        byte[] result = Util.concat(Util.intToByteArray(id),Util.intToByteArray(heapOffSet));
        writeToFile(result, id*8);
        
      }
      
      pageCounter ++;
    }
    heap.close();
    file.close();
  }
  
  private static void writeToFile(byte[] data, int position)
      throws IOException {

    
    file.seek(position);
    while(true) {
      
      byte[] bytes = new byte[8];
      file.read(bytes);
      
      if(ByteUtil.toLong(bytes) == 0 ) {
        file.seek(file.getFilePointer() - 8);
        file.write(data);
        break;
      }
    }
    
    


  }

}

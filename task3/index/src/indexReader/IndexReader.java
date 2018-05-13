package indexReader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import util.*;

public class IndexReader {
  static final String FILEPATH = "../indexFile";
  static RandomAccessFile file;
  
  public static void main(String[] args) throws IOException {
    
    file = new RandomAccessFile(FILEPATH, "rw");
    
    String input = getUserInput();
    int inputHash = input.hashCode();
    int id = (int)(Integer.toUnsignedLong(inputHash) % 3800000);
    
    
    
    
  }
  
  private static String getUserInput() {
    String input;
    Scanner reader = new Scanner(System.in);
    
    System.out.println("Enter business name to search: ");
    input = reader.nextLine();
    reader.close();
    return input;
  }
  
  
  private static byte[] readFromFile(int position) throws IOException {

    file.seek(position);
    byte[] bytes = new byte[8];
    file.read(bytes);
    file.close();
    return bytes;

  }

}

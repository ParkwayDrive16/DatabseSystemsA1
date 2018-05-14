package util;

import java.sql.Date;
import java.time.LocalDate;

public class Util {
 
   public static final byte[] intToByteArray(int value) {
       return new byte[] {
               (byte)(value >>> 24),
               (byte)(value >>> 16),
               (byte)(value >>> 8),
               (byte)value};
   }
   
   public static byte[] longToByteArray(long value) {
       return new byte[] {
           (byte) (value >> 56),
           (byte) (value >> 48),
           (byte) (value >> 40),
           (byte) (value >> 32),
           (byte) (value >> 24),
           (byte) (value >> 16),
           (byte) (value >> 8),
           (byte) value
       };
   }
   
   public static boolean isInteger(String s) {
          try { 
              Integer.parseInt(s); 
          } catch(NumberFormatException e) { 
              return false; 
          } catch(NullPointerException e) {
              return false;
          }
          // only got here if we didn't return false
          return true;
      }
   
   public static byte[] concat(byte[]...arrays)
   {
       // Determine the length of the result array
       int totalLength = 0;
       for (int i = 0; i < arrays.length; i++)
       {
           totalLength += arrays[i].length;
       }

       // create the result array
       byte[] result = new byte[totalLength];

       // copy the source arrays into the result array
       int currentIndex = 0;
       for (int i = 0; i < arrays.length; i++)
       {
           System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
           currentIndex += arrays[i].length;
       }

       return result;
   }
   
   public static LocalDate toLocDate(Date date) {
     if(date == null) {
       return null;
     } else {
       return date.toLocalDate();
     }
   }
   
}

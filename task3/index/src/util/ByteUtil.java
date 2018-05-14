package util;

import java.nio.ByteBuffer;
import java.sql.Date;

public class ByteUtil {
  
  public static String ToHex(byte[] a) {
       StringBuilder sb = new StringBuilder(a.length * 2);
       for(byte b: a)
          sb.append(String.format("%02x ", b));
       return sb.toString();
    }
  
  public static int toInt(byte[] bytes)
   {
       return ByteBuffer.wrap(bytes).getInt();
   }
  
  public static long toLong(byte[] bytes)
  {
      return ByteBuffer.wrap(bytes).getLong();
  }
  
  public static String toStatus(byte[] bytes) {
    if(ToHex(bytes) == "00") {
      return "Deregistered";
    } else {
      return "Registered";
    }
  }
  
  public static Date toDate(byte[] bytes)
  {
      long epoch = ByteBuffer.wrap(bytes).getLong();
      if (epoch == 0)
      {
          return null;
      }
      return new Date(epoch);
  }
}

package util;

import java.nio.ByteBuffer;

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
}

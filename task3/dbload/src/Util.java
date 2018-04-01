
public class Util {
    public static String byteArrayToHex(byte[] a) {
 	   StringBuilder sb = new StringBuilder(a.length * 2);
 	   for(byte b: a)
 	      sb.append(String.format("%02x ", b));
 	   return sb.toString();
 	}
 
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
}

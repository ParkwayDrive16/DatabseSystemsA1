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
	 
	 public static LocalDate toLocDate(Date date) {
			if(date == null) {
				return null;
			} else {
				return date.toLocalDate();
			}
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
}

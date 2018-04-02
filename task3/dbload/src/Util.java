import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
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
	 
	 public static int toInt(byte[] bytes)
	 {
	     return ByteBuffer.wrap(bytes).getInt();
	 }
	 
	 public static byte[] dateStringToByte(String input) throws ParseException {
		 byte[] result = new byte[8];
		 if(input.equals("")) {
		    	for(int i = 0; i < 8; i++) {
		    		result[i] = (byte) 0;
		    	}
		    } else {
		    	Date reg = df.parse(input);
		    	result = Util.longToByteArray(reg.getTime());
		    }
		 return result;
	 }
	 
	 public static byte[] stateStringtoBytes(byte[] input) {
		 
		 byte[] result = new byte[3];
		 
		 if (input.length == 0) {
			 result[0] = (byte) 0;
			 result[1] = (byte) 0;
			 result[2] = (byte) 0;
		 } else if (input.length == 2) {
	    	result[0] = input[0];
	    	result[1] = input[1];
	    	result[2] = (byte) 0;
		 } else {
			 result = input;
		 }
		 return result;
	 }
	 
    public static int isRegistered(String string) {
    	//takes status as a string and returns 0 or 1
    	if(string.equals("Deregistered")) {
    		return 0;
    	} else {
    		return 1;
    	}
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

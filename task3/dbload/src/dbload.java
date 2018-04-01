import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



public class dbload
{

    public static void main(String[] args) throws IOException, ParseException
    {
    	String[] tokens = new String[9];
	    BufferedReader input = null;
	    String inputFile;
	    String outputFile;
	    String delimiter = "\t";
	    String line;
	    int pageSize;
	    if(args.length != 0 && args[0].equals("-p")) {
	    	inputFile = args[1];
	    	pageSize = Integer.parseInt(args[2]);
	    } else {
	    	inputFile = "dataset.csv";
	    	pageSize = 4096;
	    }
	    
	    outputFile = "heap." + Integer.toString(pageSize);
	    input = new BufferedReader(new FileReader(inputFile));
	    FileOutputStream fos = new FileOutputStream(outputFile);
	    int idCounter = 0;
	    int numOfRecordsPerPage = 0;
	    byte[] numOfRecordsPerPageByte = new byte[4];
	    
	    int freeSpace = pageSize - 4;
	    int pagePointer = 0;
	    byte[] page = new byte[pageSize];
	    
	    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	    line = input.readLine();
	    
	    while ((line = input.readLine()) != null) {
	    	tokens = line.split(delimiter, -1);
	    	
	    	byte[] idBytes = intToByteArray(idCounter);
		    byte[] nameLength = intToByteArray(tokens[1].length());
		    byte[] name = tokens[1].getBytes();
		    byte[] registr = { (byte) (isRegistered(tokens[2]))};
		    
		    byte[] regDate = new byte[8];
		    if(tokens[3].equals("")) {
		    	for(int i = 0; i < 8; i++) {
		    		regDate[i] = (byte) 0;
		    	}
		    } else {
		    	Date reg = df.parse(tokens[3]);
		    	regDate = longToByteArray(reg.getTime());
		    }
		    
		    byte[] canDate = new byte[8];
		    if(tokens[4].equals("")) {
		    	for(int i = 0; i < 8; i++) {
		    		canDate[i] = (byte) 0;
		    	}
		    } else {
		    	Date can = df.parse(tokens[4]);
		    	canDate = longToByteArray(can.getTime());
		    }
		    
		    byte[] renewDate = new byte[8];
		    if(tokens[5].equals("")) {
		    	for(int i = 0; i < 8; i++) {
		    		renewDate[i] = (byte) 0;
		    	}
		    } else {
		    	Date ren = df.parse(tokens[5]);
		    	renewDate = longToByteArray(ren.getTime());
		    }
		    
		    byte[] stateNumLength = intToByteArray(tokens[6].length());
		    byte[] stateNum = tokens[6].getBytes();
		    byte[] rawState = tokens[7].getBytes();
		    byte[] state = new byte[3];
		    
		    if (rawState.length == 0) {
		    	state[0] = (byte) 0;
		    	state[1] = (byte) 0;
		    	state[2] = (byte) 0;
		    } else if (rawState.length == 2) {
		    	state[0] = rawState[0];
		    	state[1] = rawState[1];
		    	state[2] = (byte) 0;
		    } else {
		    	state = rawState;
		    }
		    
		    byte[] abnLength = intToByteArray(tokens[8].length());
		    byte[] abn = tokens[8].getBytes();
		    
	    	byte[] record = concat(idBytes, nameLength, name, registr, regDate, 
	    			canDate, renewDate, stateNumLength, stateNum, state, abnLength, abn);
		    
	    	if(freeSpace - record.length > 0) {
	    		for(int i = 0; i < record.length; i++) {
	    			page[i + pagePointer] = record[i];
	    		}
	    		freeSpace -= record.length;
	    		pagePointer += record.length;
	    		numOfRecordsPerPage++;
	    	} else {
	    		try{
	    			numOfRecordsPerPageByte = intToByteArray(numOfRecordsPerPage);
	    			for(int i = 0; i < 3; i++) {
	    				page[4093+i] = numOfRecordsPerPageByte[i];
	    			}
	    			fos.write(page);
	    			pagePointer = 0;
	    			numOfRecordsPerPage = 0;
	    			freeSpace = pageSize - 4;
	    		} catch (IOException ioe) {
	    		    ioe.printStackTrace();
	    		}
	    	}
	    	idCounter++;
	    }
	    fos.close();
	    input.close();
    }
    
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
    public static int isRegistered(String string) {
    	if(string.equals("Deregistered")) {
    		return 0;
    	} else {
    		return 1;
    	}
    }
    
//    public static byte[] dateToByte(String dateString) {
//    	byte[] result = new byte[8];
//    	
//    	if(dateString.equals("")) {
//    		for(int i = 0; i < 8; i++) {
//    			result[i] = (byte) 0;
//    		} else {
//    			Date date = df.parse(dateString);
//    		}
//    	}
//    }
}
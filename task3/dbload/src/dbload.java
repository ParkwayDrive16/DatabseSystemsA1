import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class dbload
{

    public static void main(String[] args) throws IOException, ParseException
    {
    	//Creating input and output file names
	    String inputFile;
	    String outputFile;
	    //if arguments contain '-p' then use given page size and input file
	    int pageSize = 0;
	    if(args.length != 0 && args[0].equals("-p")) {
	    	if(Util.isInteger(args[1]) && Integer.parseInt(args[1]) > 0) {
	    		pageSize = Integer.parseInt(args[1]);
	    	} else {
	    		System.out.println("Invalid page size. Using default: 4096");
	    		pageSize = 4096;
	    	}
	    	if(new File(args[2]).exists()) {
	    		inputFile = args[2];
	    	}
	    	else {
	    		System.out.println("Provided file doesn't exist. Using default: dataset.csv");
	    		inputFile = "dataset.csv";
	    	}
	    } else {
	    	//Otherwise use default values
	    	inputFile = "dataset.csv";
	    	pageSize = 4096;
	    }
	    
	    outputFile = "../heap." + Integer.toString(pageSize);
	    BufferedReader input = new BufferedReader(new FileReader(inputFile));
	    FileOutputStream fos = new FileOutputStream(outputFile);
	    
	    //informative messages
	    System.out.println("Page size: \t\t" + pageSize);
	    System.out.println("Input file: \t\t" + inputFile);
	    System.out.println("Output file: \t\t" + outputFile.substring(outputFile.length() - 9, outputFile.length()));
	    //variables to process each line of input file
	    String line;
	    String[] tokens = new String[9];
	    String delimiter = "\t";
	    //variables to help with page processing
	    int idCounter = 0;
	    int pageCounter = 0;
	    int numOfRecordsPerPage = 0;
	    int freeSpace = pageSize - 4;
	    int pagePointer = 0;
	    //creating page variable with the size given
	    byte[] page = new byte[pageSize];
	    byte[] numOfRecordsPerPageByte = new byte[4];
	    
	    long start = System.currentTimeMillis();
	    //reading and skipping the header
	    line = input.readLine();	    
	    while ((line = input.readLine()) != null) {
	    	//splitting current line with delimiter
	    	tokens = line.split(delimiter, -1);
	    	//creating byte arrays for all the data
	    	byte[] idBytes = Util.intToByteArray(idCounter);
		    byte[] nameLength = Util.intToByteArray(tokens[1].length());
		    byte[] name = tokens[1].getBytes();
		    byte[] registr = { (byte) (Util.isRegistered(tokens[2]))};
		    //creating byte arrays for dates
		    byte[] regDate = Util.dateStringToByte(tokens[3]);
		    byte[] canDate = Util.dateStringToByte(tokens[4]);
		    byte[] renewDate = Util.dateStringToByte(tokens[5]);
		    
		    byte[] stateNumLength = Util.intToByteArray(tokens[6].length());
		    byte[] stateNum = tokens[6].getBytes();
		    byte[] state = Util.stateStringtoBytes(tokens[7].getBytes());
		    byte[] abnLength = Util.intToByteArray(tokens[8].length());
		    byte[] abn = tokens[8].getBytes();
		    //concatenating all fields to 1 byte array 
	    	byte[] record = Util.concat(idBytes, nameLength, name, registr, regDate, 
	    			canDate, renewDate, stateNumLength, stateNum, state, abnLength, abn);
	    	//checking if there's enough space in the page for record created
	    	if(freeSpace - record.length > 0) {
	    		//if there is, add current record to the end of previous record using page pointer
	    		for(int i = 0; i < record.length; i++) {
	    			page[i + pagePointer] = record[i];
	    		}
	    		//updating free space, page pointer and number of pages written to this page
	    		freeSpace -= record.length;
	    		pagePointer += record.length;
	    		numOfRecordsPerPage++;
	    	} else {
	    		//case if there's not enough space in the page
	    		try{
	    			//writing number of records in bytes to the end of the page
	    			numOfRecordsPerPageByte = Util.intToByteArray(numOfRecordsPerPage);
	    			for(int i = 0; i < 4; i++) {
	    				page[pageSize-4+i] = numOfRecordsPerPageByte[i];
	    			}
	    			//writing the page to the file
	    			fos.write(page);
	    			pageCounter++;
	    			//creating new page, updating pointer to the start of the page, 
	    			//reseting free space variable and number of records in the page
	    			page = new byte[pageSize];
	    			pagePointer = 0;
	    			numOfRecordsPerPage = 0;
	    			freeSpace = pageSize - 4;
	    			//writing record to the start of the page
	    			for(int i = 0; i < record.length; i++) {
		    			page[i + pagePointer] = record[i];
		    		}
	    			//updating free space, page pointer and number of pages written to this page
		    		freeSpace -= record.length;
		    		pagePointer += record.length;
		    		numOfRecordsPerPage++;
	    		} catch (IOException ioe) {
	    		    ioe.printStackTrace();
	    		}
	    	}
	    	//updating id for each record in the input file
	    	idCounter++;
	    }
	    //when no new lines in the input file and there's data in the page
	    if(pagePointer != 0) {
	    	try{
	    		//writing number of records in bytes to the end of the page
		    	numOfRecordsPerPageByte = Util.intToByteArray(numOfRecordsPerPage);
				for(int i = 0; i < 4; i++) {
					page[pageSize-4+i] = numOfRecordsPerPageByte[i];
				}
				//writing last page to the file
				fos.write(page);
				pageCounter++;
			} catch (IOException ioe) {
			    ioe.printStackTrace();
			}
	    }
	    
	    //closing stream and buffer
	    fos.close();
	    input.close();
	    long end = System.currentTimeMillis();
	    System.out.println("\nRecords loaded:\t\t" + idCounter);
	    System.out.println("Pages used: \t\t" + pageCounter);
	    System.out.print("Execution time: \t" + (end - start) + " milliseconds.");
    }
    
}
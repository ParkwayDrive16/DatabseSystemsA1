import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.sql.Date;
import java.time.LocalDate;

public class dbquery {

	public static void main(String[] args) throws IOException {
		
		String inputFile;
		String stringToFind;
		int pageSize;
		int resultCount = 0;
		
		if(args.length != 0) {
			stringToFind = args[0];
	    	pageSize = Integer.parseInt(args[2]);
	    } else {
	    	stringToFind = "BUFFALO LOCKSMITHS";
	    	pageSize = 4096;
	    }
		
		inputFile = "../heap." + Integer.toString(pageSize);
		FileInputStream heap = new FileInputStream(inputFile);
		int pagePointer;
		byte[] page = new byte[pageSize];
		
		while (heap.read(page) > 0) {
			pagePointer = 0;
			int recordsCount = Byte.toInt(Arrays.copyOfRange(page, pageSize-4, pageSize));
			for (int i = 0; i < recordsCount; i++) {
				
				int id = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
				int nameLength = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
				String name = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += nameLength));
				String status = Byte.toStatus(Arrays.copyOfRange(page, pagePointer, pagePointer++));
				Date regDate = Byte.toDate(Arrays.copyOfRange(page, pagePointer, pagePointer += 8));
				Date canDate = Byte.toDate(Arrays.copyOfRange(page, pagePointer, pagePointer += 8));
				Date renewDate = Byte.toDate(Arrays.copyOfRange(page, pagePointer, pagePointer += 8));
				int stateNumLength = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
				String stateNum = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += stateNumLength));
				String state = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += 3));
				int abnLength = Byte.toInt(Arrays.copyOfRange(page, pagePointer, pagePointer += 4));
				String abn = new String(Arrays.copyOfRange(page, pagePointer, pagePointer += abnLength));
				
				
				if((name.toLowerCase()).contains(stringToFind.toLowerCase())) {
					businessRecord record = new businessRecord(id, name, status, toLocDate(regDate), 
							toLocDate(canDate), toLocDate(renewDate), stateNum, state, abn);
					record.printInfo();
					resultCount++;
				}
			}
		}
		System.out.println("Matches: " + resultCount);
		heap.close();
	}
	
	public static LocalDate toLocDate(Date date) {
		if(date == null) {
			return null;
		} else {
			return date.toLocalDate();
		}
	}
	
	

}

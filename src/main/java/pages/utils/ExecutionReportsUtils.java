package pages.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by comanica on 09/12/2016.
 */
public class ExecutionReportsUtils {
	
	public static void createFolderIfNotExists(String folderName){
		File theDir = new File(folderName);
		
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + folderName);
			boolean result = false;
			
			try{
				theDir.mkdir();
				result = true;
			}
			catch(Exception e){
				System.out.println("not created!!!");
			}
			if(result) {
				System.out.println("DIR created");
			}
		}
	}
	
	public static String dateToStringFormat(Date date, String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static String currentDateToStringFormat(String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(getCurrentTime());
	}
	
	public static Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}
	
	public static Date getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}
	
	
	
	
}

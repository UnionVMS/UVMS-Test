package generic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExecutionReportsUtils {
	
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

package common.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilitiy
{
	private SimpleDateFormat simpleDateFormat;
	private Date date;
	private Calendar calendar;
	private long unixTime;


	public final static String   DateInYYYYMMDDTHHMMSSZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public String getFormatedDate(String formatString){
		this.simpleDateFormat = new SimpleDateFormat(formatString);
		this.date = new Date();
		return this.simpleDateFormat.format(this.date);
	}

	public String setStartDateInYYYYMMDDTHHMMSSZ()
	{
		this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		this.date = new Date();
		return this.simpleDateFormat.format(this.date);
	}

	public String setEndDateInYYYYMMDDTHHMMSSZ()
	{
		this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		this.date = new Date();
		this.calendar = Calendar.getInstance();
		this.calendar.setTime(this.date);
		this.calendar.add(5, 5);
		this.date = this.calendar.getTime();
		return this.simpleDateFormat.format(this.date);
	}

	public String setCustomDate(String format, int days) {
		this.simpleDateFormat = new SimpleDateFormat(format);
		this.date = new Date();
		this.calendar = Calendar.getInstance();
		this.calendar.setTime(this.date);
		this.calendar.add(5, days);
		this.date = this.calendar.getTime();
		return this.simpleDateFormat.format(this.date);
	}

	public String setDateInDDMMYYYYHHMMSS()
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		this.date = new Date();
		return simpleDateFormat.format(this.date);
	}

	public String setDateInYYMMDDTHHMMSS()
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		this.date = new Date();
		return simpleDateFormat.format(this.date);
	}

	public String setDateInYYYYMMDDHHMMSS() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.date = new Date();
		return format.format(this.date);
	}

	public String convertDateFromYYYYMMDDHHMMSSToYYMMDDTHHMMSS(String dateInString) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outPutFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		this.date = inputFormat.parse(dateInString);
		return outPutFormat.format(this.date);
	}

	public String setDaysInCurrentDateAndDateInYYMMDDTHHMMSS(int days)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		this.calendar = Calendar.getInstance();
		this.calendar.add(5, days);
		this.date = this.calendar.getTime();
		return simpleDateFormat.format(this.date);
	}

	public String setMinuteInCurrentDateAndDateInYYMMDDTHHMMSS(int minutes) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		this.calendar = Calendar.getInstance();
		this.calendar.add(12, minutes);
		this.date = this.calendar.getTime();
		return simpleDateFormat.format(this.date);
	}

	public Long setStartDateInEpochFormat()
	{
		this.unixTime = System.currentTimeMillis();
		return Long.valueOf(this.unixTime);
	}

	public Long setEndDateInEpochFormat()
	{
		this.unixTime = System.currentTimeMillis();
		return Long.valueOf(this.unixTime + 1000000000L);
	}

	public String getCurrentDateInYYYYMMDD() {
		Date currDate = new Date();
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateTimeFormatter.format(currDate);
	}

	public String getDateInYYYYMMDD(Date date) {
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateTimeFormatter.format(date);
	}

	public SimpleDateFormat getSimpleDateFormat()
	{
		return this.simpleDateFormat; } 
	public Date getDate() { return this.date; } 
	public Calendar getCalendar() { return this.calendar; } 
	public long getUnixTime() { return this.unixTime; }


	public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat)
	{
		this.simpleDateFormat = simpleDateFormat; } 
	public void setDate(Date date) { this.date = date; } 
	public void setCalendar(Calendar calendar) { this.calendar = calendar; } 
	public void setUnixTime(long unixTime) { this.unixTime = unixTime; }


	public boolean equals(Object o)
	{
		if (o == this) return true; if (!(o instanceof DateUtilitiy)) return false; DateUtilitiy other = (DateUtilitiy)o; if (!other.canEqual(this)) return false; Object this$simpleDateFormat = getSimpleDateFormat(); Object other$simpleDateFormat = other.getSimpleDateFormat(); if (this$simpleDateFormat == null ? other$simpleDateFormat != null : !this$simpleDateFormat.equals(other$simpleDateFormat)) return false; Object this$date = getDate(); Object other$date = other.getDate(); if (this$date == null ? other$date != null : !this$date.equals(other$date)) return false; Object this$calendar = getCalendar(); Object other$calendar = other.getCalendar(); if (this$calendar == null ? other$calendar != null : !this$calendar.equals(other$calendar)) return false; return getUnixTime() == other.getUnixTime(); } 
	public boolean canEqual(Object other) { return other instanceof DateUtilitiy; } 
	public int hashCode() { int PRIME = 277; int result = 1; Object $simpleDateFormat = getSimpleDateFormat(); result = result * 277 + ($simpleDateFormat == null ? 0 : $simpleDateFormat.hashCode()); Object $date = getDate(); result = result * 277 + ($date == null ? 0 : $date.hashCode()); Object $calendar = getCalendar(); result = result * 277 + ($calendar == null ? 0 : $calendar.hashCode()); long $unixTime = getUnixTime(); result = result * 277 + (int)($unixTime >>> 32 ^ $unixTime); return result; } 
	public String toString() { return "DateHelper(simpleDateFormat=" + getSimpleDateFormat() + ", date=" + getDate() + ", calendar=" + getCalendar() + ", unixTime=" + getUnixTime() + ")"; }

}
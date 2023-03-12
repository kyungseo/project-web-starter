/* ============================================================================
 * [ Development Templates based on Spring Boot ]
 * ----------------------------------------------------------------------------
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================================
 * Author     Date            Description
 * --------   ----------      -------------------------------------------------
 * Kyungseo   2023-03-02      initial version
 * ========================================================================= */

package kyungseo.poc.simple.web.appcore.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


// =============
/* TEMP  */
// =============
/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class DateUtil {

    public final static String DATE_SEPARATOR = ".";

    public final static long SECOND_MILLIS = 1000;
    public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
    public final static long HOUR_MILLIS = MINUTE_MILLIS * 60;
    public final static long DAY_MILLIS = HOUR_MILLIS * 24;
    public final static long YEAR_MILLIS = DAY_MILLIS * 365;


    public static String getYear(Timestamp dateInTimestamp) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(dateInTimestamp);
    	Integer year = cal.get(Calendar.YEAR);

    	return year.toString();
    }

    public static String getMonth(Timestamp dateInTimestamp) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(dateInTimestamp);
    	Integer month = cal.get(Calendar.MONTH)+1;

    	return month.toString();
    }

    public static String getDay(Timestamp dateInTimestamp) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(dateInTimestamp);
    	Integer day = cal.get(Calendar.DATE);

    	return day.toString();
    }

    /**
     * 특정 형식의 문자열 시각을 Date 타입으로 반환한다.
     *
	 * @param format 포맷
	 * @param dateInString 날짜 스트링
     * @return Date
     */
    public static Date getDateFromString(String format, String dateInString) {
        Date resultDate = null;

        DateFormat formatter = new SimpleDateFormat(format); //"yyyy/MM/dd HH:mm:ss"

        try {
            resultDate = formatter.parse(dateInString);
            //System.out.println(formatter.format(resultDate));

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return resultDate;
    }

    public static Date getDateFromString(Object format, String dateInString) {
    	return getDateFromString((String)format, dateInString);
    }

    public static String convertDateFormat(String dateInString, String formatBefore,  String formatAfter) {
    	String dateAfter = null;

    	if (dateInString != null) {
    		Date date = getDateFromString(formatBefore, dateInString);
    		DateFormat dateFormat = new SimpleDateFormat(formatAfter);
    		dateAfter = dateFormat.format(date);
    	}

    	return dateAfter;
    }

    public static String convertDateFormat(Object dateInString, String formatBefore,  String formatAfter) {
    	return convertDateFormat((String)dateInString, formatBefore, formatAfter);
    }

    public static String convertDateFormat(Timestamp dateInTimestamp, String formatAfter) {
    	String result = null;
    	if (dateInTimestamp != null) {
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(dateInTimestamp);
	    	int year = cal.get(Calendar.YEAR);
	    	int month = cal.get(Calendar.MONTH)+1;
	    	int date = cal.get(Calendar.DATE);

	    	int hour = cal.get(Calendar.HOUR);
	    	int minute = cal.get(Calendar.MINUTE);
	    	int second = cal.get(Calendar.SECOND);

	    	StringBuffer sb = new StringBuffer();
	    	sb.append(year).append("-").append(month).append("-").append(date).append(" ").append(hour).append(":").append(minute).append(":").append(second);
	    	String dateInString = sb.toString();

	    	result = convertDateFormat(dateInString, "yyyy-MM-dd HH:mm:ss", formatAfter);
    	}
    	return result;
    }

    /**
     * 지정된 형식으로 현재 날짜 시각을 반환한다.
     *
	 * @param format 포맷
     * @return String
     */
    public static String getCurrentDate(String format) {
        String result = null;

        DateFormat dateFormat = new SimpleDateFormat(format); //"yyyy/MM/dd HH:mm:ss"
        Date date = new Date();
        result = dateFormat.format(date);
        // System.out.println(result);

        return result;
    }

    /**
     * Get the minutes difference
     *
	 * @param earlierDate 날짜A
	 * @param laterDate 날짜B
     * @return int
     */
    public static int daysDiff(Date earlierDate, Date laterDate) {
        if (earlierDate == null || laterDate == null) {
            return 0;
        }

        return (int) ((laterDate.getTime() / DAY_MILLIS) - (earlierDate.getTime() / DAY_MILLIS));
    }

    /**
     * 기준일시 부터 현재일시 까지 만료 기간이 지났는지 반환 (지났으면 true, 안지났으면 false)
     *
	 * @param expirePeriodDay 만료일시
	 * @param basicDate 기준일시
     * @return boolean
     */
    public static boolean isExpiredPeriod(int expirePeriodDay, Date basicDate) {
        boolean result = true;

        Date now = new Date();
        int daysDiff = daysDiff(basicDate, now);
        if (expirePeriodDay > daysDiff) {
            result = false;
        }

        return result;
    }

    public static String getMonthName(String month) {
    	String result = null;
    	if (month != null) {
    		result = getMonthName(Integer.parseInt(month));
    	}
    	return result;
    }

    public static String getMonthName(int month) {
    	String result = null;

    	switch (month) {
    		case 1 : result = "Jan"; break;
    		case 2 : result = "Feb"; break;
    		case 3 : result = "Mar"; break;
    		case 4 : result = "Apr"; break;
    		case 5 : result = "May"; break;
    		case 6 : result = "Jun"; break;
    		case 7 : result = "Jul"; break;
    		case 8 : result = "Aug"; break;
    		case 9 : result = "Sep"; break;
    		case 10 : result = "Oct"; break;
    		case 11 : result = "Nov"; break;
    		case 12 : result = "Dec"; break;
    	}

    	return result;
    }

    public static String getDateFormatString(int year, int month, int day, String format) {
    	String result = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
    	result = getDateFormatString(calendar, format);

    	return result;
    }

    public static String getDateFormatString(Calendar calendar, String format) {
    	String result = null;

    	if (calendar != null) {
    		SimpleDateFormat sdf = new SimpleDateFormat(format);//yyyy-MM-dd HH:mm:ss
    		result = sdf.format(calendar.getTime());
    	}

    	return result;
    }

    /**
     * 몇 분 전인지 구한다.
     *
     * 입력값 형식 : yyyy-MM-dd HH:mm
     *
	 * @param date 날짜
     * @return String
     */
    public static String getAgo(String date) {

      if (date.length() == 16) {
        date += ":00"; // hardcode
      }

      String result = "";

      // 1초전~59초전, 1분전~59분전, 1시간전~23시간전, 1일전~6일전, 1주일 전(=7일전), 8일~ 는 글쓴날짜
      try {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();
        Date date2 = df.parse(date);

        int totalSeconds = minutesDiff(date2, date1);
        // System.out.println("date1 = " + date1.getSeconds());
        // System.out.println("date2 = " + date2.getSeconds());
        // System.out.println("totalSeconds = " + totalSeconds);

        int days = totalSeconds / 86400;
        if (days != 0) {
          totalSeconds = totalSeconds - days * 86400;
        }

        int hours = totalSeconds / 3600;
        if (hours != 0) {
          totalSeconds = totalSeconds - hours * 3600;
        }

        int minutes = totalSeconds / 60;
        if (minutes != 0) {
          totalSeconds = totalSeconds - minutes * 60;
        }

        if (days != 0) {

          if (days == 7) {
            result = "일주일 전";
          }

          if (days > 7) {
            result = date.substring(0, 10);
          } else {
            result = days + "일 전";
          }

        } else if (hours != 0) {
          result = hours + "시간 전";

        } else if (minutes != 0) {
          result = minutes + "분 전";

        } else if (totalSeconds != 0) {
          result = totalSeconds + "초 전";

        } else {
          result = "조금 전";
        }

      } catch (Exception ignore) {

      }

      return result;
    }

    public static String getAgo(Object date) {
      return getAgo((String) date);
    }

    /**
     * Get the minutes difference
     *
     * @param earlierDate 날짜A
     * @param laterDate 날짜B
     * @return int
     */
    public static int minutesDiff(Date earlierDate, Date laterDate) {
      if (earlierDate == null || laterDate == null) {
        return 0;
      }

      return (int) ((laterDate.getTime() / SECOND_MILLIS) - (earlierDate.getTime() / SECOND_MILLIS));
    }

}
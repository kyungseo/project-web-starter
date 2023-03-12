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

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kyungseo.poc.simple.web.appcore.AppConstants;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class JodaTimeUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JodaTimeUtil.class);


    public static String dateToStr(Date date) {
        return dateToStr(date, AppConstants.DATETIME_FORMAT);
    }

    public static String dateToStr(Date date, String format) {
        if (date == null) {
            return null;
        }

        format = StringUtils.isBlank(format) ? AppConstants.DATETIME_FORMAT : format;
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static Date strToDate(String timeStr) {
        return strToDate(timeStr, AppConstants.DATETIME_FORMAT);
    }

    public static Date strToDate(String timeStr, String format) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }

        format = StringUtils.isBlank(format) ? AppConstants.DATETIME_FORMAT : format;

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime;
        try {
            dateTime = dateTimeFormatter.parseDateTime(timeStr);
        } catch (Exception e) {
            LOGGER.error("strToDate error: timeStr: {}", timeStr, e);
            return null;
        }

        return dateTime.toDate();
    }

    public static Boolean isTimeExpired(Date date) {
        String timeStr = dateToStr(date);
        return isBeforeNow(timeStr);
    }

    public static Boolean isTimeExpired(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return true;
        }

        return isBeforeNow(timeStr);
    }

    private static Boolean isBeforeNow(String timeStr) {
        DateTimeFormatter format = DateTimeFormat.forPattern(AppConstants.DATETIME_FORMAT);
        DateTime dateTime;
        try {
            dateTime = DateTime.parse(timeStr, format);
        } catch (Exception e) {
            LOGGER.error("isBeforeNow error: timeStr: {}", timeStr, e);
            return null;
        }
        return dateTime.isBeforeNow();
    }
    public static Date plusDays(Date date, int days) {
        return plusOrMinusDays(date, days, 0);
    }

    public static Date minusDays(Date date, int days) {
        return plusOrMinusDays(date, days, 1);
    }

    private static Date plusOrMinusDays(Date date, int days, Integer type) {
        if (null == date) {
            return null;
        }

        DateTime dateTime = new DateTime(date);
        if (type == 0) {
            dateTime = dateTime.plusDays(days);
        } else {
            dateTime = dateTime.minusDays(days);
        }

        return dateTime.toDate();
    }

    public static Date plusMinutes(Date date, int minutes) {
        return plusOrMinusMinutes(date, minutes, 0);
    }

    public static Date minusMinutes(Date date, int minutes) {
        return plusOrMinusMinutes(date, minutes, 1);
    }

    private static Date plusOrMinusMinutes(Date date, int minutes, Integer type) {
        if (null == date) {
            return null;
        }

        DateTime dateTime = new DateTime(date);
        if (type == 0) {
            dateTime = dateTime.plusMinutes(minutes);
        } else {
            dateTime = dateTime.minusMinutes(minutes);
        }

        return dateTime.toDate();
    }

    public static Date plusMonths(Date date, int months) {
        return plusOrMinusMonths(date, months, 0);
    }

    public static Date minusMonths(Date date, int months) {
        return plusOrMinusMonths(date, months, 1);
    }

    private static Date plusOrMinusMonths(Date date, int months, Integer type) {
        if (null == date) {
            return null;
        }

        DateTime dateTime = new DateTime(date);
        if (type == 0) {
            dateTime = dateTime.plusMonths(months);
        } else {
            dateTime = dateTime.minusMonths(months);
        }

        return dateTime.toDate();
    }

    public static Boolean isBetweenStartAndEndTime(Date target, Date startTime, Date endTime) {
        if (null == target || null == startTime || null == endTime) {
            return false;
        }

        DateTime dateTime = new DateTime(target);
        return dateTime.isAfter(startTime.getTime()) && dateTime.isBefore(endTime.getTime());
    }

}

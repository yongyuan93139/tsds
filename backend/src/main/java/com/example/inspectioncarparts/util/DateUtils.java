package com.example.inspectioncarparts.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public final class DateUtils {
    private static final ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>> formatMap = new ConcurrentHashMap<>();

    private DateUtils() {}

    private static SimpleDateFormat getDateFormat(final String pattern) {
        ThreadLocal<SimpleDateFormat> threadLocal = formatMap.computeIfAbsent(pattern, 
            p -> ThreadLocal.withInitial(() -> new SimpleDateFormat(p)));
        return threadLocal.get();
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return getDateFormat(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        return getDateFormat(pattern).parse(dateStr);
    }

    public static String formatNow(String pattern) {
        return format(new Date(), pattern);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        SimpleDateFormat sdf = getDateFormat("yyyy-MM-dd");
        return sdf.format(date1).equals(sdf.format(date2));
    }
}
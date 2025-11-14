package com.riyad.finance.util;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class DateUtil {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd


    public static LocalDate parseDate(String s) {
        return LocalDate.parse(s, F);
    }


    public static String formatDate(LocalDate d) {
        return d.format(F);
    }
}
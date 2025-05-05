package com.klolarion.billusserver.util.constants;

import java.time.format.DateTimeFormatter;

public class Formatter {
    public static DateTimeFormatter API_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS");
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hhmmss");
}
